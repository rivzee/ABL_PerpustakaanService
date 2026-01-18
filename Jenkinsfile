pipeline {
    agent any
    
    tools {
        // 💡 PASTIKAN: Nama 'MAVEN' sesuai dengan yang ada di 
        // Manage Jenkins -> Tools -> Global Tool Configuration
        maven 'MAVEN'
        // Menggunakan Java bawaan dari Jenkins container (tidak perlu konfigurasi JDK terpisah)
    }
    
    stages {
        stage('Checkout') {
            steps {
                echo '📥 Cloning repository...'
                checkout scm
            }
        }
        
        // ====== BUILD STAGE ======
        stage('Build All Services') {
            parallel {
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
    }
    
    post {
        success {
            echo '''
            ═══════════════════════════════════════════════════════
            ✅ BUILD & TEST SUCCESSFUL!
            ═══════════════════════════════════════════════════════
            
            All 4 microservices have been built and tested:
               ✅ Anggota Service
               ✅ Buku Service  
               ✅ Peminjaman Service
               ✅ Pengembalian Service
            
            📝 NOTE: Untuk deployment, jalankan docker-compose secara
               terpisah dari mesin lokal Anda:
               
               docker compose up -d
            ═══════════════════════════════════════════════════════
            '''
        }
        failure {
            echo '❌ BUILD OR TEST FAILED! Check the logs above for details.'
        }
        always {
            echo "🔄 Pipeline completed at: ${new Date().format('yyyy-MM-dd HH:mm:ss')}"
        }
    }
}