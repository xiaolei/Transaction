package io.github.xiaolei.transaction.event;

import java.util.Date;

/**
 * TODO: add comment
 */
public class ShowDatePickerEvent {
    public Date initialDate;

    public ShowDatePickerEvent(Date initialDate) {
        this.initialDate = initialDate;
    }
}
