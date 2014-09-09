/*********************************************************************
 *  ____                      _____      _                           *
 * / ___|  ___  _ __  _   _  | ____|_ __(_) ___ ___ ___  ___  _ __   *
 * \___ \ / _ \| '_ \| | | | |  _| | '__| |/ __/ __/ __|/ _ \| '_ \  *
 *  ___) | (_) | | | | |_| | | |___| |  | | (__\__ \__ \ (_) | | | | *
 * |____/ \___/|_| |_|\__, | |_____|_|  |_|\___|___/___/\___/|_| |_| *
 *                    |___/                                          *
 *                                                                   *
 *********************************************************************
 * Copyright 2011 Sony Ericsson Mobile Communications AB.            *
 * All rights, including trade secret rights, reserved.              *
 *********************************************************************/

/**
 * @file Evaluator.java
 *
 * @author Andreas Agvard (andreas.agvard@sonyericsson.com)
 */

package com.sonymobile.sonyselect.util.colorextraction;

/**
 * Evaluator for scoring colors for color extraction
 *
 */
public interface Evaluator {

    /**
     * Evaluates a color, returning a score.
     *
     * This function must always return the same value each time it is called
     * with the same inparameters.
     *
     * @param colorInfo ColorInfo object to evaluate
     * @return Evaluation score
     */
    public int evaluate(ColorInfo colorInfo);

}
