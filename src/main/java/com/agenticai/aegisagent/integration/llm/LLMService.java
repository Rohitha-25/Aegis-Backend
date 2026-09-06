package com.agenticai.aegisagent.integration.llm;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LLMService {

    @Autowired
    private ChatClient chatClient;

    public String generateResponse(String agentName, String agentPurpose, String question, String context) {
        String prompt = """
                You are %s, an AI agent operating within Aegis.
        
                Your purpose:
                %s
        
                Use the provided context to perform the requested task.
        
                User request:
                %s
                
                Context:
                %s
        
                Follow these rules:
                - Base your response only on the provided context.
                - Do not invent facts or information.
                - Do not use outside knowledge when answering from the context.
                - If the context is insufficient, clearly state that.
                - Provide a concise, useful response appropriate to your role.
                """.formatted(agentName, agentPurpose, question, context);

        return chatClient
                .prompt()
                .user(prompt)
                .call()
                .content();
    }
}
