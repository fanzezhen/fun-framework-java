package es;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * 单元测试：从 hits.json 中提取所有 eid 和 name
 */
class EsJsonTest {
    @Test
    void extract() throws Exception {
        try (InputStream resource = getClass().getResourceAsStream("/es/hits.json")) {
            assertNotNull(resource, "es/hits.json should be available on classpath");
            String json = new String(resource.readAllBytes(), StandardCharsets.UTF_8);
            JSONArray hits = JSON.parseArray(json);
            assertFalse(hits.isEmpty(), "hits.json should contain at least one hit");

            for (int i = 0; i < hits.size(); i++) {
                JSONObject hit = hits.getJSONObject(i);
                JSONObject source = hit.getJSONObject("_source");
                assertNotNull(source, "_source must exist for every hit");

                String eid = source.getString("eid");
                String name = source.getString("name");
                assertNotNull(eid, "eid should not be null");
                assertNotNull(name, "name should not be null");
                System.out.println(eid + "\t"+name);
            }

        }
    }
}
