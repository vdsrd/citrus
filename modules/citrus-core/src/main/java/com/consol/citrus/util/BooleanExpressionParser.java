/*
 * Copyright 2006-2009 ConSol* Software GmbH.
 * 
 * This file is part of Citrus.
 * 
 *  Citrus is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Citrus is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Citrus.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.consol.citrus.util;

import java.util.Stack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.consol.citrus.exceptions.CitrusRuntimeException;

public class BooleanExpressionParser {
    private static Stack operators = new Stack();
    private static Stack values = new Stack();

    /**
     * Logger
     */
    private static final Logger log = LoggerFactory.getLogger(BooleanExpressionParser.class);

    /**
     * @param expression
     * @throws CitrusRuntimeException
     * @return
     */
    public static boolean evaluate(String expression) {
        boolean result = true;

        char actChar;

        for (int i = 0; i < expression.length(); i++) {
            actChar = expression.charAt(i);

            if (actChar == '('){
                operators.push("(");
            } else if (actChar == ' ') {
                continue; //ignore
            } else if (actChar == ')') {
                String operator;
                while ((operator = (String)operators.pop()) != "(") {
                    String value = (String)values.pop();
                    String value2 = (String)values.pop();
                    if      (operator.equals("lt"))
                        value = Boolean.valueOf(Integer.valueOf(value2).intValue() < Integer.valueOf(value).intValue()).toString();
                    else if (operator.equals("lt="))
                        value = Boolean.valueOf(Integer.valueOf(value2).intValue() <= Integer.valueOf(value).intValue()).toString();
                    else if (operator.equals("gt"))
                        value = Boolean.valueOf(Integer.valueOf(value2).intValue() > Integer.valueOf(value).intValue()).toString();
                    else if (operator.equals("gt="))
                        value = Boolean.valueOf(Integer.valueOf(value2).intValue() >= Integer.valueOf(value).intValue()).toString();
                    else if (operator.equals("="))
                        value = Boolean.valueOf(Integer.valueOf(value2).intValue() == Integer.valueOf(value).intValue()).toString();
                    else if (operator.equals("and"))
                        value = Boolean.valueOf(Boolean.valueOf(value2).booleanValue() && Boolean.valueOf(value).booleanValue()).toString();
                    else if (operator.equals("or"))
                        value = Boolean.valueOf(Boolean.valueOf(value2).booleanValue() || Boolean.valueOf(value).booleanValue()).toString();
                    else {
                        throw new CitrusRuntimeException("Unknown operator '" + operator + "'");
                    }

                    values.push(value);
                }
            } else if (!Character.isDigit(actChar)) {
                StringBuffer operatorBuffer = new StringBuffer();

                int m = i;
                do {
                    operatorBuffer.append(actChar);
                    m++;
                } while (m < expression.length() && !Character.isDigit(actChar = expression.charAt(m)) && !(expression.charAt(m) == ' ') && !(expression.charAt(m) == '('));

                i = m - 1;

                operators.push(operatorBuffer.toString());
            } else if (Character.isDigit(actChar)) {
                StringBuffer digitBuffer = new StringBuffer();

                int m = i;
                do {
                    digitBuffer.append(actChar);
                    m++;
                } while (m < expression.length() && Character.isDigit(actChar = expression.charAt(m)));

                i = m - 1;

                values.push(digitBuffer.toString());
            }
        }

        while (!operators.isEmpty()) {
            String operator = (String)operators.pop();
            String value = (String)values.pop();
            String value2 = (String)values.pop();
            if      (operator.equals("lt"))
                value = Boolean.valueOf(Integer.valueOf(value2).intValue() < Integer.valueOf(value).intValue()).toString();
            else if (operator.equals("lt="))
                value = Boolean.valueOf(Integer.valueOf(value2).intValue() <= Integer.valueOf(value).intValue()).toString();
            else if (operator.equals("gt"))
                value = Boolean.valueOf(Integer.valueOf(value2).intValue() > Integer.valueOf(value).intValue()).toString();
            else if (operator.equals("gt="))
                value = Boolean.valueOf(Integer.valueOf(value2).intValue() >= Integer.valueOf(value).intValue()).toString();
            else if (operator.equals("="))
                value = Boolean.valueOf(Integer.valueOf(value2).intValue() == Integer.valueOf(value).intValue()).toString();
            else if (operator.equals("and"))
                value = Boolean.valueOf(Boolean.valueOf(value2).booleanValue() && Boolean.valueOf(value).booleanValue()).toString();
            else if (operator.equals("or"))
                value = Boolean.valueOf(Boolean.valueOf(value2).booleanValue() || Boolean.valueOf(value).booleanValue()).toString();
            else {
                throw new CitrusRuntimeException("Unknown operator '" + operator + "'");
            }

            values.push(value);
        }

        String value = (String)values.pop();
        result = Boolean.valueOf(value).booleanValue();

        if(log.isDebugEnabled()) {
            log.debug("Boolean expression " + expression + " evaluates to " + value);
        }

        return result;
    }
}
