package com.epam.esm.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;

public class PatchUtil {

    private PatchUtil (){}

    public static  <T> T applyPatch (JsonPatch patch, T target, Class<T> tClass) throws JsonPatchException, JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode patched = patch.apply(objectMapper.convertValue(target, JsonNode.class));
        return objectMapper.treeToValue(patched, tClass);
    }
}
