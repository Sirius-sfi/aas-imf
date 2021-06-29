package com.aibel.mel.mel2aas.templateregister;

import com.aibel.mel.mel2aas.util.URI;
import com.aibel.mel.mel2aas.util.Util;
import org.apache.poi.ss.usermodel.Row;

public enum TplRegCol {
    INSTANCE_MAP_URI("InstanceMap_Uri", true) {
        public Object getValue(Row row) {
            String strVal = Util.getStringOrDie(row, getColumnIndex());
            return new URI(strVal);
        }
    },
    TEMPLATE_URI("Template_Uri", true) {
        public Object getValue(Row row) {
            String strVal = Util.getStringOrDie(row, getColumnIndex());
            return new URI(strVal);
        }
    },
    ARGUMENT_INDEX("Argument_Index", true) {
        public Object getValue(Row row) {
            return Util.getIntegerOrDie(row, getColumnIndex());
        }
    },
    OTTR_TYPE("OTTR_Type", true) {
        public Object getValue(Row row) {
            return Util.getStringOrDie(row, getColumnIndex());
        }
    },
    VARIABLE_NAME("Variable_Name", true) {
        public Object getValue(Row row) {
            return Util.getStringOrDie(row, getColumnIndex());
        }
    },
    NON_BLANK("NonBlank", false) {
        public Object getValue(Row row) {
            String strVal = Util.getStringOrNull(row.getCell(getColumnIndex()));
            if (strVal == null) {
                return false;
            } else {
                return strVal.trim().equals("Yes");
            }
        }
    },
    OPTIONAL("Optional", false) {
        public Object getValue(Row row) {
            String strVal = Util.getStringOrNull(row.getCell(getColumnIndex()));
            if (strVal == null) {
                return false;
            } else {
                return strVal.trim().equals("Yes");
            }
        }
    },
    SELECT_DISTINCT("Select_Distinct", false) {
        public Object getValue(Row row) {
            String strVal = Util.getStringOrNull(row.getCell(getColumnIndex()));
            if (strVal == null) {
                return false;
            } else {
                return strVal.trim().equals("Yes");
            }
        }
    },
    COLUMN_NAME("Column_Name", true) {
        public Object getValue(Row row) {
            return Util.getStringOrDie(row, getColumnIndex());
        }
    },
    COLUMN_ALIAS("Column_Alias", false) {
        public Object getValue(Row row) {
            return Util.getStringOrNull(row.getCell(getColumnIndex()));
        }
    },
    VALUE_EXPRESSION("Value_Expression", false) {
        public Object getValue(Row row) {
            return Util.getStringOrNull(row.getCell(getColumnIndex()));
        }
    },
    NAMESPACE("Namespace", false) {
        public Object getValue(Row row) {
            return Util.getStringOrNull(row.getCell(getColumnIndex()));
        }
    },
    LOCAL_NAME_PREFIX("Local_Name_Prefix", false) {
        public Object getValue(Row row) {
            return Util.getStringOrNull(row.getCell(getColumnIndex()));
        }
    },
    TRIM("Trim", false) {
        public Object getValue(Row row) {
            String strVal = Util.getStringOrNull(row.getCell(getColumnIndex()));
            if (strVal == null) {
                return false;
            } else {
                return strVal.trim().equals("Yes");
            }
        }
    },
    SPACE_TO_UNDERSCORE("Space_To_Underscore", false) {
        public Object getValue(Row row) {
            String strVal = Util.getStringOrNull(row.getCell(getColumnIndex()));
            if (strVal == null) {
                return false;
            } else {
                return strVal.trim().equals("Yes");
            }
        }
    },
    H2_FILTER_CONDITION("H2FilterCondition", false) {
        public Object getValue(Row row) {
            return Util.getStringOrNull(row.getCell(getColumnIndex()));
        }
    },
    REGEXP_EXPR("Regexp_Expr", false) {
        public Object getValue(Row row) {
            return Util.getStringOrNull(row.getCell(getColumnIndex()));
        }
    },
    REGEXP_REPLACEMENT("Regexp_Replacement", false) {
        public Object getValue(Row row) {
            return Util.getStringOrNull(row.getCell(getColumnIndex()));
        }
    },
    REGEXP_FLAGS("Regexp_Flags", false) {
        public Object getValue(Row row) {
            return Util.getStringOrNull(row.getCell(getColumnIndex()));
        }
    },
    ;

    private final String columnName;
    private final boolean isRequired;

    TplRegCol(String columnName, boolean isRequired) {
        this.columnName = columnName;
        this.isRequired = isRequired;
    }

    public String getColumnName() {
        return columnName;
    }

    public boolean isRequired() {
        return isRequired;
    }

    public int getColumnIndex() {
        return ordinal();
    }

    public abstract Object getValue(Row row);

}
