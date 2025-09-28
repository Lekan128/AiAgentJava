package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.method.MethodExecutionResult;
import org.example.method.caller.ReflectionCaller;
import org.example.method.caller.ReflectionInvocableMethod;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class ProductService {
    public Product getProduct(SearchFilter filter){
        return new Product(filter.getId());
    }

    public String getProductId(Product product){
        return product.getId();
    }

    public String findProductName(String id){
        return "ProductName+"+id;
    }

    public static class SearchFilter{
        private String id;
        private Double maxPrice;
        private boolean inStockOnly;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Double getMaxPrice() {
            return maxPrice;
        }

        public void setMaxPrice(Double maxPrice) {
            this.maxPrice = maxPrice;
        }

        public boolean isInStockOnly() {
            return inStockOnly;
        }

        public void setInStockOnly(boolean inStockOnly) {
            this.inStockOnly = inStockOnly;
        }

        @Override
        public String toString() {
            return "SearchFilter{" +
                    "id='" + id + '\'' +
                    ", maxPrice=" + maxPrice +
                    ", inStockOnly=" + inStockOnly +
                    '}';
        }
    }

    public static void main(String[] args) {

        String methodList = """
                [
                 {
                  "className": "org.example.ProductService",
                  "methodName": "getProduct",
                  "methodArguments": [
                    {
                      "type": "org.example.ProductService$SearchFilter",
                      "value": {
                        "id": "1",
                        "maxPrice": "12.1",
                        "inStockOnly": "true"
                      }
                    }
                  ],
                  "returnObjectKey": "{{arg0}}"
                },
                {
                  "className": "org.example.ProductService",
                  "methodName": "getProductId",
                  "methodArguments": [
                    {
                      "type": "org.example.Product",
                      "value": "{{arg0}}"
                    }
                  ],
                  "returnObjectKey": "{{arg1}}"
                },
                {
                  "className": "org.example.ProductService",
                  "methodName": "findProductName",
                  "methodArguments": [
                    {
                      "type": "java.lang.String",
                      "value": "{{arg1}}"
                    }
                  ],
                  "returnObjectKey": "{{arg2}}"
                }
                ]
                """;

        String methodStringProduct = """
                {
                  "className": "org.example.ProductService",
                  "methodName": "getProduct",
                  "methodArguments": [
                    {
                      "type": "org.example.ProductService$SearchFilter",
                      "value": {
                        "id": "1",
                        "maxPrice": "12.1",
                        "inStockOnly": "true"
                      }
                    }
                  ],
                  "returnObjectKey": "{{arg0}}"
                }
                """;

        String methodStringProductName = """
                {
                  "className": "org.example.ProductService",
                  "methodName": "findProductName",
                  "methodArguments": [
                    {
                      "type": "java.lang.String",
                      "value": "{{arg1}}"
                    }
                  ],
                  "returnObjectKey": "{{arg2}}"
                }
                """;

        String methodStringGetProductId = """
                {
                  "className": "org.example.ProductService",
                  "methodName": "getProductId",
                  "methodArguments": [
                    {
                      "type": "org.example.Product",
                      "value": "{{arg1}}"
                    }
                  ],
                  "returnObjectKey": "{{arg1}}"
                }
                """;

        try {
            List<MethodExecutionResult> methodExecutionResults =
                    ReflectionCaller.executePipelineFromJsonList(methodList);
            System.out.println(methodExecutionResults);

        } catch (JsonProcessingException | ClassNotFoundException | InvocationTargetException | NoSuchMethodException |
                 IllegalAccessException | InstantiationException e) {
            throw new RuntimeException(e);
        }

        /*ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.setVisibility(
                com.fasterxml.jackson.annotation.PropertyAccessor.FIELD,
                com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY
        );

        ReflectionInvocableMethod productIdMethod;
        ReflectionInvocableMethod productNameMethod;
        try {
            productIdMethod = objectMapper.readValue(methodStringProduct, ReflectionInvocableMethod.class);
            Object o = ReflectionCaller.invokeMethod(productIdMethod);
            productNameMethod.getMethodArguments().forEach(map -> map.g);
            productNameMethod = objectMapper.readValue(methodStringProductName, ReflectionInvocableMethod.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }*/

        /*try {
            String o = (String) ReflectionCaller.invokeMethodFromJson(methodStringProduct);
            if (methodStringProductName.contains("$arg1$")){
                methodStringProductName=methodStringProductName.replace("$arg1$", o);
            }
            String name = (String) ReflectionCaller.invokeMethodFromJson(methodStringProductName);
            System.out.println(o);
            System.out.println(name);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }*/
    }
}
