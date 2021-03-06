/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2014 eBay Software Foundation                                                                        |
|                                                                                                                     |
|  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance     |
|  with the License.                                                                                                  |
|                                                                                                                     |
|  You may obtain a copy of the License at                                                                            |
|                                                                                                                     |
|       http://www.apache.org/licenses/LICENSE-2.0                                                                    |
|                                                                                                                     |
|  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed   |
|  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for  |
|  the specific language governing permissions and limitations under the License.                                     |
\*-------------------------------------------------------------------------------------------------------------------*/

package com.paypal.selion.utils;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.grid.common.JSONConfigurationUtils;
import org.openqa.grid.common.exception.GridConfigurationException;

/**
 * A configuration utility that is internally used by SeLion to parse sauce configuration json file.
 */
public class SauceConfigReader {

    private static final Logger LOG = Logger.getLogger(SauceConfigReader.class.getName());
    private static SauceConfigReader reader = new SauceConfigReader();
    public static final String SAUCE_CONFIG = "sauceConfig.json";

    private String authKey;
    private String sauceURL;
    private String url;
    private String userName;

    /**
     * @return - A {@link SauceConfigReader} object that can be used to retrieve values from the Configuration object as
     *         represented by the JSON file
     */
    public static SauceConfigReader getInstance() {
        return reader;
    }

    private SauceConfigReader() {
        loadConfig();
    }

    /**
     * Load the all the properties from JSON file(sauceConfig.json)
     */
    public void loadConfig() {

        try {
            JSONObject jsonObject = JSONConfigurationUtils.loadJSON(SAUCE_CONFIG);

            authKey = getAttributeValue(jsonObject, "authenticationKey");

            sauceURL = getAttributeValue(jsonObject, "sauceURL");

            String decodedKey = new String(Base64.decodeBase64(authKey));
            userName = decodedKey.substring(0, decodedKey.indexOf(":"));

            url = sauceURL + "/" + userName;

            LOG.info("Sauce Config loaded successfully");

        } catch (JSONException e) {
            String error = "Error with the JSON of the Sauce Config : " + e.getMessage();
            LOG.log(Level.SEVERE, e.getMessage(), e);
            throw new GridConfigurationException(error, e);
        }
    }

    private String getAttributeValue(JSONObject jsonObject, String key) throws JSONException {

        if (jsonObject.has(key) && !jsonObject.isNull(key)) {
            String value = jsonObject.getString(key);
            if(StringUtils.isNotBlank(value)) {
                return value;
            }
        }
        
        throw new GridConfigurationException("Invalid property " + key + " in " + SAUCE_CONFIG);
    }

    public String getAuthenticationKey() {
        LOG.info("authKey: " + authKey);
        return authKey;
    }

    public String getSauceURL() {
        LOG.info("sauceURL: " + sauceURL);
        return sauceURL;
    }

    public String getUserName() {
        LOG.info("userName: " + userName);
        return userName;
    }

    public String getURL() {
        LOG.info("url: " + url);
        return url;
    }

}
