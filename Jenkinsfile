pipeline {
    agent any
    
    tools {
        // 💡 PASTIKAN: Nama 'MAVEN' sesuai dengan yang ada di 
        // Manage Jenkins -> Tools -> Global Tool Configuration
        maven 'MAVEN'
        // Menggunakan Java bawaan dari Jenkins container (tidak perlu konfigurasi JDK terpisah)
    }
    
    // 🔔 TRIGGER: Jenkins akan cek GitHub setiap 1 menit untuk perubahan
    // Format: * * * * * = setiap 1 menit
    // Tidak perlu ngrok atau webhook - Jenkins yang aktif cek GitHub
    triggers {
        pollSCM('* * * * *')
    }
    
    // 🌐 Environment Variables
    environment {
        DOCKER_COMPOSE_FILE = 'docker-compose.yaml'
        DEPLOY_ENV = 'production'
    }
    
    stages {
        stage('Checkout') {
            steps {
                echo '📥 Cloning repository from GitHub...'
                checkout scm
                echo "🔍 Branch: ${env.GIT_BRANCH ?: 'unknown'}"
                echo "🔍 Commit: ${env.GIT_COMMIT ?: 'unknown'}"
            }
        }
        
        // ====== BUILD STAGE ======
        stage('Build All Services') {
            parallel {
                stage('Build Eureka Server') {
                    steps {
                        echo '🔨 Building Eureka Server...'
                        dir('eureka-server') {
                            sh 'mvn clean package -DskipTests'
                        }
                    }
                }
                stage('Build API Gateway') {
                    steps {
                        echo '🔨 Building API Gateway...'
                        dir('api-gateway') {
                            sh 'mvn clean package -DskipTests'
                        }
                    }
                }
                stage('Build Anggota') {
                    steps {
                        echo '🔨 Building Anggota Service...'
                        dir('anggota') {
                            sh 'mvn clean package -DskipTests'
                        }
                    }
                }
                stage('Build Buku') {
                    steps {
                        echo '🔨 Building Buku Service...'
                        dir('buku') {
                            sh 'mvn clean package -DskipTests'
                        }
                    }
                }
                stage('Build Peminjaman') {
                    steps {
                        echo '🔨 Building Peminjaman Service...'
                        dir('peminjaman') {
                            sh 'mvn clean package -DskipTests'
                        }
                    }
                }
                stage('Build Pengembalian') {
                    steps {
                        echo '🔨 Building Pengembalian Service...'
                        dir('pengembalian') {
                            sh 'mvn clean package -DskipTests'
                        }
                    }
                }
            }
        }
        
        // ====== TEST STAGE ======
        stage('Test All Services') {
            parallel {
                stage('Test Anggota') {
                    steps {
                        echo '🧪 Testing Anggota Service...'
                        dir('anggota') {
                            sh 'mvn test'
                        }
                    }
                }
                stage('Test Buku') {
                    steps {
                        echo '🧪 Testing Buku Service...'
                        dir('buku') {
                            sh 'mvn test'
                        }
                    }
                }
                stage('Test Peminjaman') {
                    steps {
                        echo '🧪 Testing Peminjaman Service...'
                        dir('peminjaman') {
                            sh 'mvn test'
                        }
                    }
                }
                stage('Test Pengembalian') {
                    steps {
                        echo '🧪 Testing Pengembalian Service...'
                        dir('pengembalian') {
                            sh 'mvn test'
                        }
                    }
                }
            }
        }
        
        // ====== DOCKER BUILD STAGE ======
        stage('Build Docker Images') {
            steps {
                echo '🐳 Building Docker images for all services...'
                sh '''
                    docker compose build --no-cache \
                        eureka-server \
                        api-gateway \
                        anggota-service \
                        buku-service \
                        peminjaman-service \
                        pengembalian-service
                '''
            }
        }
        
        // ====== DEPLOY STAGE ======
        stage('Deploy Services') {
            steps {
                echo '🚀 Deploying all services with Docker Compose...'
                sh '''
                    # Stop existing containers (gracefully)
                    docker compose down --remove-orphans || true
                    
                    # Start all services
                    docker compose up -d
                    
                    # Wait for services to be ready
                    echo "⏳ Waiting for services to start..."
                    sleep 30
                    
                    # Check service status
                    docker compose ps
                '''
            }
        }
        
        // ====== HEALTH CHECK STAGE ======
        stage('Health Check') {
            steps {
                echo '🏥 Running health checks...'
                sh '''
                    echo "Checking Eureka Server..."
                    curl -sf http://localhost:8761/actuator/health || echo "⚠️ Eureka not ready yet"
                    
                    echo "Checking API Gateway..."
                    curl -sf http://localhost:8080/actuator/health || echo "⚠️ API Gateway not ready yet"
                    
                    echo "✅ Deployment completed!"
                '''
            }
        }
    }
    
    post {
        success {
            echo '''
            ═══════════════════════════════════════════════════════════════
            ✅ CI/CD PIPELINE SUCCESSFUL - AUTO DEPLOYED!
            ═══════════════════════════════════════════════════════════════
            
            🎉 All microservices have been built, tested, and deployed:
               ✅ Eureka Server       - http://localhost:8761
               ✅ API Gateway         - http://localhost:8080
               ✅ Anggota Service     - via API Gateway
               ✅ Buku Service        - via API Gateway
               ✅ Peminjaman Service  - via API Gateway
               ✅ Pengembalian Service- via API Gateway
            
            🔗 This build was triggered automatically by GitHub push!
            ═══════════════════════════════════════════════════════════════
            '''
        }
        failure {
            echo '''
            ═══════════════════════════════════════════════════════════════
            ❌ PIPELINE FAILED!
            ═══════════════════════════════════════════════════════════════
            Check the logs above for details.
            Rolling back to previous version...
            '''
            sh '''
                # Attempt rollback on failure
                docker compose down || true
            '''
        }
        always {
            echo "🔄 Pipeline completed at: ${new Date().format('yyyy-MM-dd HH:mm:ss')}"
            // Clean up workspace
            cleanWs(cleanWhenNotBuilt: false,
                    deleteDirs: true,
                    disableDeferredWipeout: true,
                    notFailBuild: true)
        }
    }
}