/*
 * RHQ Management Platform
 * Copyright (C) 2005-2014 Red Hat, Inc.
 * All rights reserved.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License, version 2, as
 * published by the Free Software Foundation, and/or the GNU Lesser
 * General Public License, version 2.1, also as published by the Free
 * Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License and the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU General Public License
 * and the GNU Lesser General Public License along with this program;
 * if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */

package org.rhq.core.domain.drift.dto;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.rhq.core.domain.drift.DriftChangeSet;
import org.rhq.core.domain.drift.DriftChangeSetCategory;
import org.rhq.core.domain.drift.DriftConfigurationDefinition.DriftHandlingMode;

public class DriftChangeSetDTO implements DriftChangeSet<DriftDTO>, Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    private Long ctime;

    private int version;

    private DriftChangeSetCategory category;

    private int driftDefId;

    private DriftHandlingMode driftHandlingMode;

    private int resourceId;

    private Set<DriftDTO> drifts = new HashSet<DriftDTO>();

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public Long getCtime() {
        return ctime;
    }

    public void setCtime(Long ctime) {
        this.ctime = ctime;
    }

    @Override
    public int getVersion() {
        return version;
    }

    @Override
    public void setVersion(int version) {
        this.version = version;
    }

    @Override
    public DriftChangeSetCategory getCategory() {
        return category;
    }

    @Override
    public void setCategory(DriftChangeSetCategory category) {
        this.category = category;
    }

    @Override
    public int getDriftDefinitionId() {
        return driftDefId;
    }

    @Override
    public void setDriftDefinitionId(int id) {
        driftDefId = id;
    }

    @Override
    public int getResourceId() {
        return resourceId;
    }

    @Override
    public void setResourceId(int id) {
        resourceId = id;
    }

    @Override
    public Set<DriftDTO> getDrifts() {
        return drifts;
    }

    @Override
    public void setDrifts(Set<DriftDTO> drifts) {
        this.drifts = drifts;
    }

    @Override
    public DriftHandlingMode getDriftHandlingMode() {
        return this.driftHandlingMode;
    }

    @Override
    public void setDriftHandlingMode(DriftHandlingMode mode) {
        this.driftHandlingMode = mode;
    }
}
