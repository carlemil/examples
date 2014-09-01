/*
 * Copyright (C) 2013 Sony Mobile Communications AB.
 * All rights, including trade secret rights, reserved.
 */
package com.sonymobile.userlogginglib.internal.dispatch;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Interface for the classes the {@link DispatcherHelper} can talk to. Used to
 * enable easier mocking when testing.
 */
public interface LoggDestination {

    public OutputStream open() throws IOException;

    public void close() throws IOException;

}
