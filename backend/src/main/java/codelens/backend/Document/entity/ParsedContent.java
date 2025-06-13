package codelens.backend.Document.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParsedContent {
    private String title;
    private List<CodeBlock> codeBlocks;
    private List<Section> sections;
    private List<String> bulletPoints;
    private String originalContent;
}