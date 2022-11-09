/*
 * Copyright (C) 2022 CLARIN
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
package eu.clarin.cmdi.vlo.mapping.processing;

import eu.clarin.cmdi.vlo.mapping.model.ValueLanguagePair;
import java.util.stream.Stream;

/**
 * Processes mapping results for a field into a set of values that actually will
 * be stored in that field. This is to be applied after all transformations on
 * record values have been carried  out. Typical use cases are value 
 * harmonisation/normalisation within a single field.
 *
 * @author CLARIN ERIC <clarin@clarin.eu>
 */
public interface SingleFieldValuesProcessor {

    Stream<ValueLanguagePair> process(String field, Iterable<ValueLanguagePair> values);

}
