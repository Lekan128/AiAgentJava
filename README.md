# AiAgentJava
Simple Ai Agent in Java

How to use.

Tools the Ai can use are in methods.
To make the Ai aware of the method (tool), the method must be annotated with `@AiToolMethod`
NOTE: `@AiToolMethod` has an argument, which is the description of the method.
`@ArgDesc` is used to describe the argument that the method will take
You can also add `@Nullable` to the parameter to tell the Ai that the parameter is not required, it null can be passed.

For example:
```Java
    @AiToolMethod("Get the name of a user's top product with user id")
    public String getUsersTopProductName(@ArgDesc("User id") String id){
        return "Samsung galaxy s25 Ultra";
    }
```