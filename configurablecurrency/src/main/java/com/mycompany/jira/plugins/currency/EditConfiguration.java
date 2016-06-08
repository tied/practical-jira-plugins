package com.mycompany.jira.plugins.currency;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import com.atlassian.jira.config.managedconfiguration.ManagedConfigurationItemService;
import com.atlassian.jira.web.action.admin.customfields.AbstractEditConfigurationItemAction;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EditConfiguration extends AbstractEditConfigurationItemAction {

	private static final long serialVersionUID = 816164801314288847L;

	public EditConfiguration(ManagedConfigurationItemService managedConfigurationItemService) {
    	super(managedConfigurationItemService);
    }

    // This is not used in the sample but would be used in production
    // @RequiresXsrfCheck
    protected void doValidation() {
        String lstr = getLocalestr();
        log.debug("Entering doValidation with " + lstr);
        if (lstr == null) {
            // Nothing to check yet until a choice is submitted
            return;
        }
        String[] parts = lstr.split("_");
        if (parts.length < 1 || parts.length > 3) {
            addErrorMessage("Unable to parse the Locale string: " + lstr + " has " + parts.length + " parts");
        }
    }

    // This is not used in the sample but would be used in production
    // @RequiresXsrfCheck
    protected String doExecute() throws Exception {
        // We could handle a cancel by setting the same value again

        // The first time the page is loaded retrieve any existing configuration
        if (getLocalestr() == null) {
            setLocalestr(DAO.retrieveStoredValue(getFieldConfig()));
        }

        log.debug("Chosen locale is: " + getLocalestr());

        // We could handle any errors and return to the input screen again
        // but there shouldn't be any after doValidate was called
        // return ERROR;

        // Save the configured currency choice
        DAO.updateStoredValue(getFieldConfig(), getLocalestr());

        // Redirect to the custom field configuration screen
        String save = getHttpRequest().getParameter("Save");
        if (save != null && save.equals("Save")) {
            setReturnUrl("/secure/admin/ConfigureCustomField!default.jspa?customFieldId=" + getFieldConfig().getCustomField().getIdAsLong().toString());
            return getRedirect("not used");
        }

        return INPUT;
    }

    // The chosen locale (as a string) and its currency. The format is
    // Language[_Country[_Variant]]. For example, en_US
    private String localestr;

    // This method is set when the HTML form is submitted because
    // the name of the HTML input element is "localestr"
    public void setLocalestr(String value) {
        this.localestr = value;
    }

    public String getLocalestr() {
        return this.localestr;
    }

    /**
     *
     * @return a List of Locale strings and the currency for each locale.
     */
    public List<CurrencyOption> getCurrencies() {
        List<CurrencyOption> result = new ArrayList<CurrencyOption>();
        Map<String, Locale> sortedLocales = getSortedLocales();
        for (Locale locale : sortedLocales.values()) {
            try {
                StringBuffer sb = new StringBuffer();
                sb.append(locale.getLanguage());                
                if (!locale.getCountry().equals("")) {
                    sb.append("_");
                    sb.append(locale.getCountry());
                }
                if (!locale.getVariant().equals("")) {
                    sb.append("_");
                    sb.append(locale.getVariant());
                }

                String displayValue = DAO.getDisplayValue(locale);
                result.add(new CurrencyOption(sb.toString(), displayValue));
            } catch (IllegalArgumentException iae) {
                // If the country of the given locale is not a
                // supported ISO 3166 country code.
                continue;
            }
        }
        return result;
    }
    
    /**
     * Sort the locales by country name.
     *
     * @return a Map of Locale country name and Locale
     */
    private Map<String, Locale> getSortedLocales() {
        Locale[] locales = Locale.getAvailableLocales();
        Map<String, Locale> sortedLocales = new TreeMap<String, Locale>();
        for (Locale locale : locales) {
            if (locale == null) {
                continue;
            }
            if (locale.getCountry() == null) {
                continue;
            }

            // Make sure that a language exists. Does Antartica
            // have a language?
            if (locale.getLanguage().equals("")) {
                continue;
            }
            sortedLocales.put(locale.getCountry(), locale);
        }
        return sortedLocales;
    }

    public boolean isSelected(String localestr) {
        String current = DAO.retrieveStoredValue(getFieldConfig());
        if (current == null || current.equals("")) {
            return false;
        }
        return current.equals(localestr);
    }

    /**
     * Used only by this class.
     */
    public class CurrencyOption {
        
        private String id;
        private String displayValue;
        
        public CurrencyOption(String id, String displayValue) {
            this.id = id;
            this.displayValue = displayValue;
        }
        
        public String getId() {
            return id;
        }
    
        public String getDisplayValue() {
            return displayValue;
        }
        
    }

}
