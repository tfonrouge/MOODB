package test01.data.tableBase;

import tech.fonrouge.ui.UI_CtrlRecord;

public abstract class TableBaseCtrlRecord<T extends TableBase, U extends TableBaseData<T>> extends UI_CtrlRecord<T> {

    @Override
    protected String getCtrlFXMLPath() {
        return "record.fxml";
    }
}
