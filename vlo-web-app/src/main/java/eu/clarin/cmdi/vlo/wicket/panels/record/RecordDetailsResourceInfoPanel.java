/*
 * Copyright (C) 2019 CLARIN
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package eu.clarin.cmdi.vlo.wicket.panels.record;

import com.google.common.collect.ImmutableList;

import eu.clarin.cmdi.vlo.FieldKey;
import eu.clarin.cmdi.vlo.config.FieldNameService;
import eu.clarin.cmdi.vlo.service.ResourceStringConverter;
import eu.clarin.cmdi.vlo.wicket.BooleanVisibilityBehavior;
import eu.clarin.cmdi.vlo.wicket.LazyResourceInfoUpdateBehavior;
import eu.clarin.cmdi.vlo.wicket.components.PIDLinkLabel;
import eu.clarin.cmdi.vlo.wicket.components.ResourceAvailabilityWarningBadge;
import eu.clarin.cmdi.vlo.wicket.components.ResourceTypeIcon;
import eu.clarin.cmdi.vlo.wicket.model.BooleanOptionsModel;
import eu.clarin.cmdi.vlo.wicket.model.IsPidModel;
import eu.clarin.cmdi.vlo.wicket.model.PIDContext;
import eu.clarin.cmdi.vlo.wicket.model.PIDLinkModel;
import eu.clarin.cmdi.vlo.wicket.model.ResolvingLinkModel;
import eu.clarin.cmdi.vlo.wicket.model.ResourceInfoModel;
import eu.clarin.cmdi.vlo.wicket.model.SolrFieldModel;
import eu.clarin.cmdi.vlo.wicket.model.SolrFieldStringModel;
import static eu.clarin.cmdi.vlo.wicket.pages.RecordPage.RESOURCES_SECTION;
import eu.clarin.cmdi.vlo.wicket.panels.BootstrapDropdown;

import java.util.Optional;

import org.apache.solr.common.SolrDocument;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * a panel for 'core links' (landing page and/or single resource)
 *
 * @author Twan Goosen <twan@clarin.eu>
 */
public abstract class RecordDetailsResourceInfoPanel extends GenericPanel<SolrDocument> {

    private static final int PID_LABEL_TEXT_LENGTH = 25;

    @SpringBean
    private FieldNameService fieldNameService;
    @SpringBean(name = "resourceStringConverter")
    private ResourceStringConverter resourceStringConverter;
    @SpringBean(name = "resolvingResourceStringConverter")
    private ResourceStringConverter resolvingResourceStringConverter;

    private final SolrFieldModel<String> resourcesModel;
    private final ResourceInfoModel resourceInfoModel;
    private final ResourceInfoModel landingPageResourceInfoModel;
    private final ResolvingLinkModel resourceInfoLinkModel;

    private final IModel<Boolean> landingPageVisibilityModel;
    private final IModel<Boolean> resourceInfoVisibilityModel;
    private final IModel<Boolean> resourcesLinkVisibilityModel;

    public RecordDetailsResourceInfoPanel(String id, IModel<SolrDocument> model) {
        super(id, model);

        resourcesModel = new SolrFieldModel<>(model, fieldNameService.getFieldName(FieldKey.RESOURCE));
        resourceInfoModel = new ResourceInfoModel(resourceStringConverter, new SolrFieldStringModel(model, fieldNameService.getFieldName(FieldKey.RESOURCE)));

        landingPageResourceInfoModel
                = new ResourceInfoModel(
                        resourceStringConverter,
                        new SolrFieldStringModel(getModel(), fieldNameService.getFieldName(FieldKey.LANDINGPAGE)));

        // resource info for single resource
        resourceInfoLinkModel
                = ResolvingLinkModel.modelFor(resourceInfoModel, getModel());

        // visibility models
        landingPageVisibilityModel
                = () -> (landingPageResourceInfoModel.getObject() != null
                && landingPageResourceInfoModel.getObject().getHref() != null);

        resourceInfoVisibilityModel
                = () -> (resourcesModel.getObject() != null
                && resourcesModel.getObject().size() == 1
                && resourceInfoLinkModel.getObject() != null);

        resourcesLinkVisibilityModel
                = () -> (resourcesModel.getObject() != null
                && resourcesModel.getObject().size() > 1);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize(); //To change body of generated methods, choose Tools | Templates.

        add(createLandingPageLink("landingPage")
                .add(BooleanVisibilityBehavior.visibleOnTrue(landingPageVisibilityModel)));

        add(createSingleResourceInfo("resourceInfo")
                .add(BooleanVisibilityBehavior.visibleOnTrue(resourceInfoVisibilityModel)));

        add(createMultipleResourceLink("resourcesInfo")
                .add(BooleanVisibilityBehavior.visibleOnTrue(resourcesLinkVisibilityModel)));

        add(createResourcesTitle("resourcesTitle"));
    }

    private Component createResourcesTitle(String id) {
        final IModel<String> titleModel = new BooleanOptionsModel<>(
                () -> (resourcesModel.getObject() != null && resourcesModel.getObject().size() == 1),
                Model.of("Linked resource"),
                Model.of("Linked resources")
        );

        return new Label(id, titleModel).add(new Behavior() {
            @Override
            public void onConfigure(Component component) {
                component.setVisible(
                        landingPageVisibilityModel.getObject() // landing page link is visible
                        && (resourceInfoVisibilityModel.getObject() || resourcesLinkVisibilityModel.getObject()) // and resource(s) info as well
                );
            }

        });
    }

    private Component createLandingPageLink(String id) {
        final IModel<String> linkModel = new PropertyModel<>(landingPageResourceInfoModel, "href");
        final IsPidModel isPidModel = new IsPidModel(linkModel);
        final PIDLinkModel pidLinkModel = PIDLinkModel.wrapLinkModel(linkModel);

        final AjaxFallbackLink showResourcesLink = new AjaxFallbackLink<Void>("showResources") {
            @Override
            public void onClick(Optional<AjaxRequestTarget> target) {
                switchToTab(RESOURCES_SECTION, target);
            }
        };

        final WebMarkupContainer availabilityWarning = new ResourceAvailabilityWarningBadge("availabilityWarning", landingPageResourceInfoModel);
        showResourcesLink.add(availabilityWarning);

        return new WebMarkupContainer(id)
                .add(showResourcesLink)
                .add(new ExternalLink("landingPageLink", pidLinkModel)
                        .add(new Label("landingPageLinkLabel", linkModel)
                                .add(BooleanVisibilityBehavior.visibleOnFalse(isPidModel))))
                .add(new PIDLinkLabel("landingPagePidLabel", pidLinkModel, Model.of(PIDContext.LANDING_PAGE), PID_LABEL_TEXT_LENGTH)
                        .add(BooleanVisibilityBehavior.visibleOnTrue(isPidModel)));
    }

    /**
     * Information component for a single linked resource
     *
     * @param id
     * @param linkModel
     * @return
     */
    private Component createSingleResourceInfo(String id) {
        final WebMarkupContainer resourceInfo = new WebMarkupContainer(id);

        // Resource info for single resource (should not appear if there are more or fewer resources)
        resourceInfo.add(new ExternalLink("resourceLink", new PIDLinkModel(ResolvingLinkModel.modelFor(resourceInfoModel, getModel())))
                //resource type icon
                .add(new ResourceTypeIcon("resourceTypeIcon", new PropertyModel<>(resourceInfoModel, "resourceType"))
                        //with type name tooltip
                        .add(new AttributeModifier("title", new StringResourceModel("resourcetype.${resourceType}.singular", this, resourceInfoModel)
                                .setDefaultValue(new PropertyModel<>(resourceInfoModel, "resourceType")))))
                //resource name below icon
                .add(new Label("resourceName", new PropertyModel<>(resourceInfoModel, "fileName")))
        );

        resourceInfo
                .add(new PIDLinkLabel("pidLabel", resourceInfoLinkModel, Model.of(PIDContext.RESOURCE), PID_LABEL_TEXT_LENGTH)
                        .add(BooleanVisibilityBehavior.visibleOnTrue(
                                new IsPidModel(resourceInfoLinkModel))));

        // Resource info gets async update to resolve any handle to a file name
        resourceInfo.add(new LazyResourceInfoUpdateBehavior(resolvingResourceStringConverter, resourceInfoModel) {

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                target.add(resourceInfo);
            }
        });
        final AjaxFallbackLink<Void> showResourcesLink = new AjaxFallbackLink<>("showResources") {
            @Override
            public void onClick(Optional<AjaxRequestTarget> target) {
                switchToTab(RESOURCES_SECTION, target);
            }
        };

        final WebMarkupContainer availabilityWarning = new ResourceAvailabilityWarningBadge("availabilityWarning", resourceInfoModel);

        final PropertyModel<Boolean> availabilityWarningModel = new PropertyModel<>(resourceInfoModel, "availabilityWarning");
        showResourcesLink
                .add(availabilityWarning
                        .add(BooleanVisibilityBehavior.visibleOnTrue(availabilityWarningModel)));
        resourceInfo.add(showResourcesLink);

        //dropdown menu for LRS connection
        resourceInfo
                .add(new ResourceLinkOptionsDropdown("dropdown", getModel(), resourceInfoLinkModel, resourceInfoModel) {
                    @Override
                    protected void createDropdownOptions(ImmutableList.Builder<BootstrapDropdown.DropdownMenuItem> listBuilder) {
                        listBuilder.add(createResourceInfoDropdownOption());
                        super.createDropdownOptions(listBuilder);
                    }
                });

        resourceInfo.setOutputMarkupId(true);
        return resourceInfo;
    }

    private BootstrapDropdown.DropdownMenuItem createResourceInfoDropdownOption() {
        return new BootstrapDropdown.DropdownMenuItem("Show resource details", "glyphicon glyphicon-info-sign") {
            @Override
            protected Link getLink(String id) {
                return new Link<>(id) {

                    @Override
                    public void onClick() {
                        switchToTab(RESOURCES_SECTION, Optional.empty());
                    }
                };
            }
        };
    }

    private Component createMultipleResourceLink(String id) {
        return new WebMarkupContainer(id)
                .add(new AjaxFallbackLink<Void>("showResources") {
                    @Override
                    public void onClick(Optional<AjaxRequestTarget> target) {
                        switchToTab(RESOURCES_SECTION, target);
                    }
                }.add(new Label("resourcesCount", new PropertyModel<String>(resourcesModel, "size"))));
    }

    protected abstract void switchToTab(String tab, Optional<AjaxRequestTarget> target);

}
