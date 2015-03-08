package com.klarna.ondemand;

/**
 * Builds a RegistrationSettings object
 */
public class RegistrationSettingsBuilder {

    String confirmedUserDataId;
    String prefillPhoneNumber;

    /**
     * Sets confirmed user data id parameter
     * @param confirmedUserDataId an identifier for pre-confirmed user data
     * @return this registration settings builder object
     */
    public RegistrationSettingsBuilder setConfirmedUserDataId(String confirmedUserDataId) {
        this.confirmedUserDataId = confirmedUserDataId;

        return this;
    }

    /**
     * Sets pre-fill phone number parameter
     * @param prefillPhoneNumber phone number of the user. This number will be pre-filled in the registration activity.
     * @return this registration settings builder object
     */
    public RegistrationSettingsBuilder setPrefillPhoneNumber(String prefillPhoneNumber) {
        this.prefillPhoneNumber = prefillPhoneNumber;

        return this;
    }

    /**
     * Creates a RegistrationSettings object from this builder.
     * @return registration settings object
     */
    public RegistrationSettings build() {
        return new RegistrationSettings(this);
    }
}
