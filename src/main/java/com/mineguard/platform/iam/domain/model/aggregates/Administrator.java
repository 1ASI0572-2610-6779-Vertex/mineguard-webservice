package com.mineguard.platform.iam.domain.model.aggregates;

import com.mineguard.platform.iam.domain.model.commands.CreateAdministratorCommand;
import com.mineguard.platform.iam.domain.model.valueobjects.AccessStatus;
import com.mineguard.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;
import lombok.Setter;

/**
 * Administrator aggregate root.
 * Perfil de alto nivel que gestiona la plataforma y crea a los supervisores.
 */
@Getter
public class Administrator extends AbstractDomainAggregateRoot<Administrator> {

    @Setter private Long id;
    @Setter private String fullName;
    @Setter private String email;
    @Setter private AccessStatus accessStatus;
    @Setter private Long userId;

    public Administrator() {}

    public Administrator(String fullName, String email, AccessStatus accessStatus, Long userId) {
        this.fullName = fullName;
        this.email = email;
        this.accessStatus = accessStatus;
        this.userId = userId;
    }

    public Administrator(CreateAdministratorCommand command) {
        this(command.fullName(), command.email(), AccessStatus.ACTIVE, command.userId());
    }

    public Administrator updateInformation(String fullName, String email, AccessStatus accessStatus) {
        this.fullName = fullName;
        this.email = email;
        if (accessStatus != null) {
            this.accessStatus = accessStatus;
        }
        return this;
    }
}