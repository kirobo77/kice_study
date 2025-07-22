import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class RegexFileParser {
    protected String fileName;
    protected List<String> lines;
    
    public RegexFileParser(String fileName) {
        this.fileName = fileName;
        this.lines = new ArrayList<>();
        loadFile();
    }
    
    /**
     * 파일을 읽어서 lines 리스트에 저장
     */
    private void loadFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            System.err.println("파일을 읽는 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 파일이 성공적으로 로드되었는지 확인
     * @return 파일 로드 성공 여부
     */
    public boolean isFileLoaded() {
        return !lines.isEmpty();
    }
    
    /**
     * 로드된 파일의 전체 라인 수 반환
     * @return 라인 수
     */
    public int getLineCount() {
        return lines.size();
    }
    
    /**
     * 특정 라인 반환
     * @param lineNumber 라인 번호 (0부터 시작)
     * @return 해당 라인의 문자열, 범위를 벗어나면 null
     */
    public String getLine(int lineNumber) {
        if (lineNumber >= 0 && lineNumber < lines.size()) {
            return lines.get(lineNumber);
        }
        return null;
    }
    
    /**
     * 모든 라인을 하나의 문자열로 결합
     * @return 전체 파일 내용
     */
    public String getAllContent() {
        StringBuilder content = new StringBuilder();
        for (String line : lines) {
            content.append(line).append("\n");
        }
        return content.toString();
    }
    
    /**
     * 특정 문자열이 포함된 라인들을 반환
     * @param searchText 검색할 문자열
     * @return 해당 문자열이 포함된 라인들의 리스트
     */
    public List<String> findLinesContaining(String searchText) {
        List<String> matchingLines = new ArrayList<>();
        for (String line : lines) {
            if (line.contains(searchText)) {
                matchingLines.add(line);
            }
        }
        return matchingLines;
    }
    
    /**
     * 파일명 반환
     * @return 파일명
     */
    public String getFileName() {
        return fileName;
    }
    
    /**
     * 파일을 다시 로드
     */
    public void reloadFile() {
        lines.clear();
        loadFile();
    }
    
    /**
     * 파일 정보 출력
     */
    public void printFileInfo() {
        System.out.println("파일명: " + fileName);
        System.out.println("전체 라인 수: " + getLineCount());
        System.out.println("파일 로드 상태: " + (isFileLoaded() ? "성공" : "실패"));
    }
    
}