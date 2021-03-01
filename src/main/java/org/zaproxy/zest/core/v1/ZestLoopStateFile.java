/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package org.zaproxy.zest.core.v1;

/** The Class ZestLoopStateFile. */
public class ZestLoopStateFile extends ZestLoopStateString {

    /** Instantiates a new zest loop state file. */
    public ZestLoopStateFile() {
        super();
    }

    public ZestLoopStateFile(ZestLoopTokenFileSet set) {
        super(set.getConvertedSet());
    }
}