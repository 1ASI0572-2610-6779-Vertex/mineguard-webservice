package com.mineguard.platform.iam.domain.model.aggregates;

import com.mineguard.platform.iam.domain.model.commands.CreateSupervisorCommand;
import com.mineguard.platform.iam.domain.model.valueobjects.AccessStatus;
import com.mineguard.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;
import lombok.Setter;

/**
 * Supervisor aggregate root.
 *
 * <p>Backs the web "Gestion de Usuarios" admin directory. A supervisor account
 * is created by an administrator (not via self-service) and carries an access
 * status that supports security lockout and logical off-boarding.</p>
 */
@Getter
public class Supervisor extends AbstractDomainAggregateRoot<Supervisor> {

    @Setter
    private Long id;
    @Setter
    private String fullName;
    @Setter
    private String corporateId;
    @Setter
    private String email;
    @Setter
    private AccessStatus accessStatus;

    public Supervisor() {
    }

    public Supervisor(String fullName, String corporateId, String email, AccessStatus accessStatus) {
        this.fullName = fullName;
        this.corporateId = corporateId;
        this.email = email;
        this.accessStatus = accessStatus;
    }

    public Supervisor(CreateSupervisorCommand command) {
        this(command.fullName(), command.corporateId(), command.email(), AccessStatus.ACTIVE);
    }

    /** Updates the editable directory fields. */
    public Supervisor updateInformation(String fullName, String corporateId, String email, AccessStatus accessStatus) {
        this.fullName = fullName;
        this.corporateId = corporateId;
        this.email = email;
        if (accessStatus != null) {
            this.accessStatus = accessStatus;
        }
        return this;
    }
}
