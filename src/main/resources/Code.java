class X {
    encode(){
        org.junit.Assert.assertEquals(a.b.c.D.E, parsed);
        if(org.eclipse.xtext.xbase.lib.IterableExtensions.isNullOrEmpty(tokens)){return"";} 
        java.nio.ByteBuffer buffer=java.nio.ByteBuffer.allocate((((com.google.common.collect.Iterables.size(tokens))*2)*4));
        for(org.eclipse.lsp4j.util.SemanticHighlightingTokens.Token token:tokens){
            int character=token.character;int length=token.length;
            int scope=token.scope;
            int lengthAndScope=length;
            lengthAndScope=lengthAndScope<<(org.eclipse.lsp4j.util.SemanticHighlightingTokens.LENGTH_SHIFT); 
            lengthAndScope|=scope;buffer.putInt(character); 
            buffer.putInt(lengthAndScope); 
        }
        org . junit . Assert . assertEquals ( software . amazon . awssdk . protocols . json . AwsJsonErrorMessageParserTest . MESSAGE_CONTENT , parsed );
        assertEquals( a.b.c.D.E, parsed);
        return java.util.Base64.getEncoder().encodeToString(buffer.array());
    }
}
