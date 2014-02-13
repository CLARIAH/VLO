/*
 * Copyright (C) 2014 CLARIN
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
package eu.clarin.cmdi.vlo.service.impl;

import eu.clarin.cmdi.vlo.pojo.FacetSelection;
import eu.clarin.cmdi.vlo.pojo.QueryFacetsSelection;
import eu.clarin.cmdi.vlo.service.FacetFieldsService;
import java.util.Iterator;
import java.util.List;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

/**
 *
 * @author twagoo
 */
public class FacetFieldsDataProvider implements IDataProvider<FacetField> {

    private final FacetFieldsService facetFieldService;
    private final IModel<List<FacetSelection>> selectionModel;
    private final IModel<String> searchQuery;

    public FacetFieldsDataProvider(FacetFieldsService facetFieldService, IModel<QueryFacetsSelection> model) {
        this(facetFieldService,
                new PropertyModel<List<FacetSelection>>(model, "selection"),
                new PropertyModel<String>(model, "query"));
    }

    public FacetFieldsDataProvider(FacetFieldsService facetFieldService, IModel<List<FacetSelection>> selectionModel, IModel<String> searchQuery) {
        this.facetFieldService = facetFieldService;
        this.selectionModel = selectionModel;
        this.searchQuery = searchQuery;
    }

    @Override
    public Iterator<? extends FacetField> iterator(long first, long count) {
        List<FacetField> facets = facetFieldService.getFacetFields(selectionModel.getObject(), searchQuery.getObject());
        return facets.listIterator((int) first);
    }

    @Override
    public long size() {
        return facetFieldService.getFacetFieldCount();
    }

    @Override
    public IModel<FacetField> model(FacetField object) {
        return Model.of(object);
    }

    @Override
    public void detach() {
    }

}
