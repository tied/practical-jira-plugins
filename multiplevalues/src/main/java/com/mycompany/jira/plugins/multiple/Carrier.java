package com.mycompany.jira.plugins.multiple;

/**
 * This is the singular object for this custom field type.
 * It has a Double field for amounts and a String field
 * for comments about the the amount.
 */
public class Carrier {
    
    private Double amount;
    private String note;
    
    public Carrier(Double amount, String note) {
        this.amount = amount;
        this.note = note;
    }
    
    public Double getAmount() {
        return amount;
    }
    
    public String getNote() {
        return note;
    }
    
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Carrier object: ");
        sb.append(amount);
        sb.append(" and ");
        sb.append(note);
        return sb.toString();
    }
    
}

// Note: Velocity can still reference this class even it is defined as
// a class within MultipleValuesCFType