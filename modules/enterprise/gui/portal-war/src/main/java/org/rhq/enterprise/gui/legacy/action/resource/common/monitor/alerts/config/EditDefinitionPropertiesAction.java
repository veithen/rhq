/*
 * RHQ Management Platform
 * Copyright (C) 2005-2008 Red Hat, Inc.
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
package org.rhq.enterprise.gui.legacy.action.resource.common.monitor.alerts.config;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import org.rhq.core.domain.alert.AlertDefinition;
import org.rhq.core.domain.alert.AlertPriority;
import org.rhq.core.domain.auth.Subject;
import org.rhq.enterprise.gui.legacy.ParamConstants;
import org.rhq.enterprise.gui.legacy.action.BaseAction;
import org.rhq.enterprise.gui.legacy.action.resource.ResourceForm.FormContext;
import org.rhq.enterprise.gui.legacy.action.resource.common.monitor.alerts.AlertDefUtil;
import org.rhq.enterprise.gui.legacy.util.RequestUtils;
import org.rhq.enterprise.server.alert.AlertDefinitionException;
import org.rhq.enterprise.server.alert.AlertDefinitionManagerLocal;
import org.rhq.enterprise.server.alert.AlertTemplateManagerLocal;
import org.rhq.enterprise.server.alert.GroupAlertDefinitionManagerLocal;
import org.rhq.enterprise.server.util.LookupUtil;

/**
 * Create a new alert definition.
 */
public class EditDefinitionPropertiesAction extends BaseAction {
    private Log log = LogFactory.getLog(EditDefinitionPropertiesAction.class);

    @Override
    @SuppressWarnings("deprecation")
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
        HttpServletResponse response) throws Exception {
        DefinitionForm defForm = (DefinitionForm) form;
        FormContext context = defForm.getContext();
        log.trace("defForm.id=" + defForm.getId());

        Map<String, Integer> params = new HashMap<String, Integer>();
        params.put("ad", defForm.getAd());

        if (context == FormContext.Type) {
            params.put(ParamConstants.RESOURCE_TYPE_ID_PARAM, defForm.getType());
        } else if (context == FormContext.Resource) {
            params.put(ParamConstants.RESOURCE_ID_PARAM, defForm.getId());
        } else if (context == FormContext.Group) {
            params.put(ParamConstants.GROUP_ID_PARAM, defForm.getGroupId());
        } else {
            throw new IllegalArgumentException("Unsupported context: " + context);
        }

        ActionForward forward = checkSubmit(request, mapping, form, params);
        if (forward != null) {
            log.trace("returning " + forward);
            return forward;
        }

        Subject subject = RequestUtils.getSubject(request);
        AlertDefinition alertDef = AlertDefUtil.getAlertDefinition(request);

        alertDef.setName(defForm.getName());
        alertDef.setDescription(defForm.getDescription());
        alertDef.setPriority(AlertPriority.values()[defForm.getPriority()]);
        alertDef.setEnabled(defForm.isActive());

        try {
            if (context == FormContext.Type) {
                AlertTemplateManagerLocal alertTemplateManager = LookupUtil.getAlertTemplateManager();
                alertTemplateManager.updateAlertTemplate(subject, alertDef, false);
            } else if (context == FormContext.Resource) {
                AlertDefinitionManagerLocal alertDefinitionManager = LookupUtil.getAlertDefinitionManager();
                // this will disallow updates if the alert definition has been deleted
                alertDef.setReadOnly(defForm.isReadOnly());
                alertDefinitionManager.updateAlertDefinition(subject, alertDef.getId(), alertDef, false);
            } else if (context == FormContext.Group) {
                GroupAlertDefinitionManagerLocal groupAlertDefinitionManager = LookupUtil
                    .getGroupAlertDefinitionManager();
                groupAlertDefinitionManager.updateGroupAlertDefinitions(subject, alertDef, false);
            } else {
                throw new IllegalArgumentException("Unsupported context: " + context);
            }
        } catch (AlertDefinitionException iade) {
            log.debug("alert definition update failed:", iade);
            RequestUtils.setError(request, "alert.config.edit.definition.error", iade.getMessage(), "global");
            return returnFailure(request, mapping, null);
        }

        log.trace("returning success");
        return returnSuccess(request, mapping, params);
    }
}