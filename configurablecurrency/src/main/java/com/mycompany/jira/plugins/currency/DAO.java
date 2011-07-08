package com.mycompany.jira.plugins.currency;

import com.atlassian.jira.issue.fields.config.FieldConfig;
import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.module.propertyset.PropertySetManager;
import java.util.Locale;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 * DAO - Data Access Object
 */
public class DAO {

    public static final Logger log = Logger.getLogger(DAO.class);
    private static PropertySet ofbizPs = null;

    /*
     * The next id is stored in OSPropertyEntry in SEQUENCE_VALUE_ITEM
     * but this one was chosen at random.
     */
    private static final int ENTITY_ID = 20000;

    private static PropertySet getPS() {
        if (ofbizPs == null) {
            HashMap ofbizArgs = new HashMap();
            ofbizArgs.put("delegator.name", "default");
            ofbizArgs.put("entityName", "currency_fields");
            ofbizArgs.put("entityId", new Long(ENTITY_ID));
            ofbizPs = PropertySetManager.getInstance("ofbiz", ofbizArgs);
        }
        return ofbizPs;
    }

    /**
     *
     * @param fieldConfig should not be null
     */
    private static String getEntityName(FieldConfig fieldConfig) {
        Long context = fieldConfig.getId();
        String psEntityName = fieldConfig.getCustomField().getId() + "_" + context + "_config";
        return psEntityName;
    }

    public static String retrieveStoredValue(FieldConfig fieldConfig) {
        String entityName = getEntityName(fieldConfig);
        return getPS().getString(entityName);
    }

    public static void updateStoredValue(FieldConfig fieldConfig, String value) {
        String entityName = getEntityName(fieldConfig);
        getPS().setString(entityName, value);
    }


    /**
     * @return the current symbol, no ISO code
     */
    public static String getCurrentSymbol(FieldConfig fieldConfig) {
        Locale locale = getCurrentLocale(fieldConfig);
        return getCurrentSymbol(locale);
    }
    
    /**
     * @return the current Locale
     */
    public static Locale getCurrentLocale(FieldConfig fieldConfig) {
        String localeStr = retrieveStoredValue(fieldConfig);
        log.debug("Current stored locale string: " + localeStr);
        Locale locale = Locale.getDefault();
        try {
            if (localeStr != null && !localeStr.equals("")) {
                String[] parts = localeStr.split("_");
                if (parts.length == 3) {
                    locale = new Locale(parts[0], parts[1], parts[2]);
                } else if (parts.length == 2) {
                    locale = new Locale(parts[0], parts[1]);
                } else if (parts.length == 1) {
                    locale = new Locale(parts[0]);
                } else {
                    log.error("Unable to parse the Locale string: " + localeStr + " has " + parts.length + " parts");
                }
            }
        } catch (Exception ex) {
            // NPE from Locale ctor. Use the default Locale already created
            log.error("Unable to construct a Locale from " + localeStr);
        }
        log.debug("Current locale is: " + locale);

        return locale;
    }
    
    /**
     * @return the currency symbol for the given Locale in that Locale
     */
    public static String getCurrentSymbol(Locale locale) {
        Currency currency = Currency.getInstance(locale);
        return currency.getSymbol(locale);
    }
    
    /**
     * @return the full description of a currency in the given Locale
     */
    public static String getDisplayValue(Locale locale) {
        Currency currency = Currency.getInstance(locale);
        StringBuffer sb = new StringBuffer();
        sb.append(currency.getSymbol());
        if (!locale.equals(Locale.getDefault())) {
            sb.append(" (");
            // Use the symbol as it is shown in its own locale, otherwise
            // it's just the code.
            sb.append(currency.getSymbol(locale));
            sb.append(")");
        }
        sb.append(" - ");
        sb.append(locale.getDisplayCountry());
        return sb.toString();
    }
    
}
