/*
 * Copyright (c) 2013 Tah Wei Hoon.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License Version 2.0,
 * with full text available at http://www.apache.org/licenses/LICENSE-2.0.html
 *
 * This software is provided "as is". Use at your own risk.
 */
package com.myopicmobile.textwarrior.common

class Flag {
    @get:Synchronized
    var isSet: Boolean = false
        private set

    @Synchronized
    fun set() {
        this.isSet = true
    }

    @Synchronized
    fun clear() {
        this.isSet = false
    }
}
