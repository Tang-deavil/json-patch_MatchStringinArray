/*
 * Copyright (c) 2014, Francis Galiegue (fgaliegue@gmail.com)
 *
 * This software is dual-licensed under:
 *
 * - the Lesser General Public License (LGPL) version 3.0 or, at your option, any
 *   later version;
 * - the Apache Software License (ASL) version 2.0.
 *
 * The text of this file and of both licenses is available at the root of this
 * project or, if you have the jar distribution, in directory META-INF/, under
 * the names LGPL-3.0.txt and ASL-2.0.txt respectively.
 *
 * Direct link to the sources:
 *
 * - LGPL 3.0: https://www.gnu.org/licenses/lgpl-3.0.txt
 * - ASL 2.0: http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package com.github.fge.jsonpatch.jsonpointer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.github.fge.jsonpatch.JsonPatchOperation;

import javax.annotation.concurrent.Immutable;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Implementation of {@link TokenResolver} for {@link JsonNode}
 *
 * <p>The JSON Pointer specification specifies that for arrays, indices must
 * not have leading zeroes (save for {@code 0} itself). This class handles
 * this.</p>
 */
@Immutable
public final class JsonNodeResolver
    extends TokenResolver<JsonNode>
{
    /**
     * Zero
     */
    private static final char ZERO = '0';

    public JsonNodeResolver(final ReferenceToken token)
    {
        super(token);
    }

    @Override
    public JsonNode get(final JsonNode node)
    {
        if (node == null || !node.isContainerNode())
            return null;
        final String raw = token.getRaw();
        return node.isObject() ? node.get(raw) : node.get(arrayIndexFor(raw,node));
    }

    /**
     * Return an array index corresponding to the given (raw) reference token
     *
     * <p>If no array index can be found, -1 is returned. As the result is used
     * with {@link JsonNode#get(int)}, we are guaranteed correct results, since
     * this will return {@code null} in this case.</p>
     *
     * @param raw the raw token, as a string
     * @return the index, or -1 if the index is invalid
     */
    private static int arrayIndexFor(final String raw,final JsonNode node)
    {
        /*
         * Empty? No dice.
         */
        if (raw.isEmpty())
            return -1;
        /*
         * Leading zeroes are not allowed in number-only refTokens for arrays.
         * But then, 0 followed by anything else than a number is invalid as
         * well. So, if the string starts with '0', return 0 if the token length
         * is 1 or -1 otherwise.
         */
        if (raw.charAt(0) == ZERO)
            return raw.length() == 1 ? 0 : -1;

        /*
         * Otherwise, parse as an int. If we can't, -1.
         */
        try {
//            String node2 =
//            Pattern pattern2 =Pattern.compile("[[]]+");
            //            List<JsonNode> strs2 =pattern2.split(node);
            Pattern pattern =Pattern.compile("[.=)]+");
            String[] strs =pattern.split(raw);
            String node2 ="{\"node\""+":"+node.toString()+"}";
            ArrayNode arrNode = (ArrayNode) new ObjectMapper().readTree(node2).get("node");
            if(arrNode.isArray()){
                for (JsonNode object : arrNode){
                    int index = 0 ;
                    System.out.println(object);
                    if(strs[2].equals(object.findValue(strs[1]).asText())){
                        return index;
                    }
                    index++;
                }
            }

            JsonNode step1 = node.findValue(strs[1]);
            JsonNode step2 = node.get(strs[1]+":"+strs[2]);
            JsonNode step3 = node.path(strs[1]+":"+strs[2]);
            JsonNode step4 = node.findValue(strs[2]);
            JsonNode step5 = node.get(strs[1]+":"+strs[2]);
            JsonNode step6 = node.path(strs[1]+":"+strs[2]);
            JsonNode step7 = node.get("\"id\": \"abc\"");
            JsonNode step8 = node.path("\"id\": \"abc\"");

            for (int i=0 ; i<strs.length; i++) {
                System.out.println(strs[i]);
//                System.out.println(strs2);
                System.out.println(node);
                System.out.println(step1);
                System.out.println(step2);
                System.out.println(step3);
                System.out.println(step4);
                System.out.println(step5);
                System.out.println(step6);
                System.out.println(step7);
                System.out.println(step8);


            }
            return Integer.parseInt(raw);

        } catch (NumberFormatException | JsonProcessingException ignored) {
            return -1;
        }
    }
}
