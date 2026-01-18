package com.vanzi.peminjaman.application.commands;

import com.vanzi.peminjaman.application.Command;
import com.vanzi.peminjaman.dto.CommandResult;

/**
 * Command for deleting a Peminjaman
 * Returns CommandResult (CQRS compliant)
 */
public class DeletePeminjamanCommand implements Command<CommandResult> {
    private final Long id;

    public DeletePeminjamanCommand(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
