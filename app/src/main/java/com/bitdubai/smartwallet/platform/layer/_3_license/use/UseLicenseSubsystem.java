package com.bitdubai.smartwallet.platform.layer._3_license.use;

import com.bitdubai.smartwallet.platform.layer._3_license.CantStartSubsystemException;
import com.bitdubai.smartwallet.platform.layer._3_license.LicenseManager;
import com.bitdubai.smartwallet.platform.layer._3_license.LicenseSubsystem;
import com.bitdubai.smartwallet.platform.layer._3_license.use.developer.DeveloperBitDubai;

/**
 * Created by ciencias on 21.01.15.
 */
public class UseLicenseSubsystem implements LicenseSubsystem {

    private LicenseManager mLicenseManager;

    @Override
    public LicenseManager getLicenseManager() {
        return mLicenseManager;
    }

    @Override
    public void start() throws CantStartSubsystemException {
        /**
         * I will choose from the different versions available of this functionality.
         */

        try {
            DeveloperBitDubai developerBitDubai = new DeveloperBitDubai();
            mLicenseManager = developerBitDubai.getLicenseManager();
        }
        catch (Exception e)
        {
            System.err.println("Exception: " + e.getMessage());
            throw new CantStartSubsystemException();
        }
    }


}
