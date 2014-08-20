/*
 * RHQ Management Platform
 * Copyright (C) 2005-2014 Red Hat, Inc.
 * All rights reserved.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation version 2 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */
package org.rhq.enterprise.server.storage.maintenance.step;

import java.util.Set;

import org.rhq.core.domain.storage.MaintenanceStep;
import org.rhq.enterprise.server.auth.SubjectManagerLocal;
import org.rhq.enterprise.server.cloud.StorageNodeManagerLocal;
import org.rhq.enterprise.server.operation.OperationManagerLocal;
import org.rhq.enterprise.server.storage.StorageClientManager;
import org.rhq.enterprise.server.storage.maintenance.StorageMaintenanceJob;

/**
 * @author Stefan Negrea
 *
 */
public interface MaintenanceStepRunner {

    void setClusterSnapshot(Set<String> clusterSnapshot);

    void setStep(MaintenanceStep step);

    void execute() throws StepFailureException;

    StepFailureStrategy getFailureStrategy();

    /**
     * <p>
     * Invoked when the step fails and the runner has a failure strategy of {@link StepFailureStrategy#CONTINUE}. This
     * method is responsible for creating new job to be scheduled for the failed step. The job will be added to the
     * maintenance queue for later execution.
     * </p>
     * <p>
     * Note that null can be returned if it is unnecessary to create a new job.
     * </p>
     */
    StorageMaintenanceJob createNewJobForFailedStep();

    void setStorageClientManager(StorageClientManager storageClientManager);

    void setStorageNodeManager(StorageNodeManagerLocal storageNodeManager);

    void setOperationManager(OperationManagerLocal operationManager);

    void setSubjectManager(SubjectManagerLocal subjectManager);
}
