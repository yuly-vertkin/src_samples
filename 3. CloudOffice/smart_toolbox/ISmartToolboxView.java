/*
 * Copyright (c) New Cloud Technologies, Ltd., 2013-2017
 *
 * You can not use the contents of the file in any way without New Cloud Technologies, Ltd. written permission.
 * To obtain such a permit, you should contact New Cloud Technologies, Ltd. at http://ncloudtech.com/contact.html
 *
 */

package com.ncloudtech.cloudoffice.android.myoffice.toolbox;

interface ISmartToolboxView {
    /**
     * Set view selected / deselected
     * @param id
     * @param selected
     */
    void setViewSelected(int id, boolean selected);

    /**
     * Set view enabled / disabled
     * @param id
     * @param enabled
     */
    void setViewEnabled(int id, boolean enabled);

    /**
     * Set view visibility
     * @param visible
     */
    void setVisibility(boolean visible);
}
