/*
 * Copyright (C) 2013 Sony Mobile Communications AB.
 * All rights, including trade secret rights, reserved.
 */
package com.sonymobile.userlogginglib.runners;

import com.sonymobile.userlogginglib.test.LoggerTestSuite;

import android.test.InstrumentationTestRunner;

import junit.framework.TestSuite;

public class UnitTestRunner extends InstrumentationTestRunner {

    @Override
    public TestSuite getAllTests() {

        return new LoggerTestSuite(this);
    }

}
