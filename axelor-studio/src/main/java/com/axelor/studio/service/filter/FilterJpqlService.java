/*
 * Axelor Business Solutions
 *
 * Copyright (C) 2018 Axelor (<http://axelor.com>).
 *
 * This program is free software: you can redistribute it and/or  modify
 * it under the terms of the GNU Affero General Public License, version 3,
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.axelor.studio.service.filter;

import com.axelor.meta.db.MetaField;
import com.axelor.meta.db.MetaJsonField;
import com.axelor.studio.db.Filter;
import com.google.inject.Inject;
import java.lang.invoke.MethodHandles;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This service class use to generate groovy expression from chart filters.
 *
 * @author axelor
 */
public class FilterJpqlService {

  private final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  @Inject private FilterCommonService filterCommonService;

  public String getJpqlFilters(List<Filter> filterList) {

    String filters = null;

    if (filterList == null) {
      return filters;
    }

    for (Filter filter : filterList) {

      MetaField field = filter.getMetaField();

      String relationship = field.getRelationship();
      String fieldName =
          relationship != null ? filter.getTargetField() : filter.getMetaField().getName();
      String condition =
          filterCommonService.getCondition(
              "self." + fieldName, filter.getOperator(), filter.getValue());

      if (filters == null) {
        filters = condition;
      } else {
        String opt = filter.getLogicOp() != null && filter.getLogicOp() == 0 ? " AND " : " OR ";
        filters = filters + opt + condition;
      }
    }

    log.debug("JPQL filter: {}", filters);
    return filters;
  }

  public String getJsonJpql(MetaJsonField jsonField) {

    switch (jsonField.getType()) {
      case "integer":
        return "json_extract_integer";
      case "decimal":
        return "json_extract_decimal";
      case "boolean":
        return "json_extract_boolean";
      default:
        return "json_extract";
    }
  }
}