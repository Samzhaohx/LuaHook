/*
 * Copyright (c) 2013 Tah Wei Hoon.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License Version 2.0,
 * with full text available at http://www.apache.org/licenses/LICENSE-2.0.html
 *
 * This software is provided "as is". Use at your own risk.
 */
package com.myopicmobile.textwarrior.common

import android.util.Log

class TextWarriorException(msg: String?) : Exception(msg) {
    companion object {
        private const val NDEBUG = false // set to true to suppress assertions
        private val serialVersionUID = -8393914265675038931L

        @JvmStatic
		fun fail(details: String) {
            assertVerbose(false, details)
        }

        @JvmStatic
		fun assertVerbose(condition: Boolean, details: String) {
            if (NDEBUG) {
                return
            }

            if (!condition) {
                /* BlackBerry dialog way of displaying errors
		        UiApplication.getUiApplication().invokeLater(new Runnable()
		        {
		            public void run()
		            {
		                Dialog.alert(details);
		            }
		        });
		    */

                /* For Android, a Context has to be passed into this method
			 * to display the error message on the device screen */

                System.err.print("TextWarrior assertion failed: ")
                System.err.println(details)
                Log.i("lua", details)
            }
        }
    }
}
