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
package eu.clarin.cmdi.vlo.importer.linkcheck;

import eu.clarin.cmdi.rasa.DAO.CheckedLink;
import java.io.Closeable;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.stream.Stream;

/**
 *
 * @author Twan Goosen <twan@clarin.eu>
 */
public interface ResourceAvailabilityStatusChecker extends Closeable {

    Map<String, CheckedLink> getLinkStatusForRefs(Stream<String> hrefs) throws IOException;
    
    void writeStatusSummary(Writer writer) throws IOException;
}
