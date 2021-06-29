package com.aibel.mel.mel2aas.propertymap;

import com.aibel.mel.mel2aas.meltoc.TocEntry;

public enum ColHdr {
    CLASS_OF_ACTIVITY("CLASS_OF_ACTIVITY", 0, false),
    CLASS_OF_FUNCTIONAL_OBJECT("CLASS_OF_FUNCTIONAL_OBJECT", 1, false),
    CLASS_OF_INANIMATE_PHYSICAL_OBJECT("CLASS_OF_INANIMATE_PHYSICAL_OBJECT", 2, false),
    DESCRIPTION("Description", 3, false),
    OTTR_TEMPLATE_IRI("OTTR_Template_IRI", 4, true),
    SUBJECT_COLUMN("Subject_Column", 5, true),
    TYPE_COLUMN("Type_Column", 6, false),
    OBJECT_COLUMN("Object_Column", 7, false),
    ;

    private final String columnName;
    private final int columnIndex;
    private final boolean isRequired;

    ColHdr(String columnName, int columnIndex, boolean isRequired) {
        this.columnName = columnName;
        this.columnIndex = columnIndex;
        this.isRequired = isRequired;
    }

    public String getColumnName() {
        return columnName;
    }

    public int getColumnIndex() {
        return columnIndex;
    }

    public boolean isRequired() {
        return isRequired;
    }

    public boolean matches(TocEntry tocEntry) {
        return tocEntry.getColumnName().equals(getColumnName());
    }

}
