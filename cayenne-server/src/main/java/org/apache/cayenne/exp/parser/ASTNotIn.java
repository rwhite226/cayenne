/*****************************************************************
 *   Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 ****************************************************************/


package org.apache.cayenne.exp.parser;

import org.apache.cayenne.exp.Expression;
import org.apache.commons.collections.Transformer;

/**
 * "Not In" expression.
 * 
 */
public class ASTNotIn extends ConditionNode {
    ASTNotIn(int id) {
        super(id);
    }

    public ASTNotIn() {
        super(ExpressionParserTreeConstants.JJTNOTIN);
    }

    public ASTNotIn(SimpleNode path, ASTList list) {
        super(ExpressionParserTreeConstants.JJTNOTIN);
        jjtAddChild(path, 0);
        jjtAddChild(list, 1);
        connectChildren();
    }

    @Override
    protected int getRequiredChildrenCount() {
        return 2;
    }

    @Override
    protected boolean evaluateSubNode(Object o, Object[] evaluatedChildren) throws Exception {
        if (o == null || evaluatedChildren[1] == null) {
            return false;
        }

        Object[] objects = (Object[]) evaluatedChildren[1];
        for (Object object : objects) {
            if (object != null && Evaluator.evaluator(o).eq(o, object)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Creates a copy of this expression node, without copying children.
     */
    @Override
    public Expression shallowCopy() {
        return new ASTNotIn(id);
    }

    @Override
    protected String getExpressionOperator(int index) {
        return "not in";
    }

    @Override
    public int getType() {
        return Expression.NOT_IN;
    }
    
    @Override
    protected Object transformExpression(Transformer transformer) {
        Object transformed = super.transformExpression(transformer);
        
        // transform empty ASTNotIn to ASTTrue
        if (transformed instanceof ASTNotIn) {
            ASTNotIn exp = (ASTNotIn) transformed;
            if (exp.jjtGetNumChildren() == 2) {
                ASTList list = (ASTList) exp.jjtGetChild(1);
                Object[] objects = (Object[]) list.evaluate(null);
                if (objects.length == 0) {
                    transformed = new ASTTrue();
                }
            }
        }

        return transformed;
    }

}
