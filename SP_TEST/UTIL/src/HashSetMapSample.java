import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class HashSetMapSample {
    
    // 문제 1: 중복 제거 및 정렬
    // 배열에서 중복을 제거하고 오름차순으로 정렬하여 출력
    public static void problem1() {
        System.out.println("=== 문제 1: 중복 제거 및 정렬 ===");
        int[] arr = {5, 2, 8, 2, 9, 1, 5, 6};
        
        // TreeSet 사용으로 중복 제거와 정렬을 동시에
        Set<Integer> uniqueSet = new TreeSet<>();
        for (int num : arr) {
            uniqueSet.add(num);
        }
        
        System.out.print("결과: ");
        for (int num : uniqueSet) {
            System.out.print(num + " ");
        }
        System.out.println();
    }
    
    // 문제 2: 문자열에서 각 문자의 빈도수 계산
    public static void problem2() {
        System.out.println("\n=== 문제 2: 문자 빈도수 계산 ===");
        String str = "programming";
        
        Map<Character, Integer> charCount = new HashMap<>();
        
        for (char c : str.toCharArray()) {
            charCount.put(c, charCount.getOrDefault(c, 0) + 1);
        }
        
        System.out.println("문자열: " + str);
        System.out.println("빈도수:");
        for (Map.Entry<Character, Integer> entry : charCount.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }
    
    // 문제 3: 두 배열의 교집합 구하기
    public static void problem3() {
        System.out.println("\n=== 문제 3: 두 배열의 교집합 ===");
        int[] arr1 = {1, 2, 3, 4, 5};
        int[] arr2 = {4, 5, 6, 7, 8};
        
        Set<Integer> set1 = new HashSet<>();
        for (int num : arr1) {
            set1.add(num);
        }
        
        Set<Integer> intersection = new HashSet<>();
        for (int num : arr2) {
            if (set1.contains(num)) {
                intersection.add(num);
            }
        }
        
        System.out.println("배열1: " + Arrays.toString(arr1));
        System.out.println("배열2: " + Arrays.toString(arr2));
        System.out.println("교집합: " + intersection);
    }
    
    // 문제 4: 학생 성적 관리 시스템
    static class Student {
        String name;
        int score;
        
        Student(String name, int score) {
            this.name = name;
            this.score = score;
        }
        
        @Override
        public String toString() {
            return name + "(" + score + ")";
        }
    }
    
    public static void problem4() {
        System.out.println("\n=== 문제 4: 학생 성적 관리 ===");
        
        Map<String, Student> students = new HashMap<>();
        students.put("김철수", new Student("김철수", 85));
        students.put("이영희", new Student("이영희", 92));
        students.put("박민수", new Student("박민수", 78));
        students.put("정수진", new Student("정수진", 95));
        
        // 성적순으로 정렬 (내림차순)
        List<Student> sortedStudents = new ArrayList<>(students.values());
        sortedStudents.sort((s1, s2) -> s2.score - s1.score);
        
        System.out.println("성적순 정렬 결과:");
        for (Student student : sortedStudents) {
            System.out.println(student);
        }
        
        // 특정 점수 이상인 학생 찾기
        System.out.println("\n90점 이상인 학생:");
        for (Student student : students.values()) {
            if (student.score >= 90) {
                System.out.println(student);
            }
        }
    }
    
    // 문제 5: 그룹 애너그램 (해시맵 활용)
    public static void problem5() {
        System.out.println("\n=== 문제 5: 그룹 애너그램 ===");
        String[] words = {"eat", "tea", "tan", "ate", "nat", "bat"};
        
        Map<String, List<String>> anagramGroups = new HashMap<>();
        
        for (String word : words) {
            // 문자를 정렬하여 키로 사용
            char[] chars = word.toCharArray();
            Arrays.sort(chars);
            String key = new String(chars);
            
            anagramGroups.computeIfAbsent(key, k -> new ArrayList<>()).add(word);
        }
        
        System.out.println("입력 배열: " + Arrays.toString(words));
        System.out.println("애너그램 그룹:");
        for (List<String> group : anagramGroups.values()) {
            if (group.size() > 1) {
                System.out.println(group);
            }
        }
    }
    
    // 문제 6: LRU 캐시 구현 (LinkedHashMap 활용)
    // LRU(Least Recently Used) : 가장 최근에 사용되지 않은 캐시 교체
    static class LRUCache {
        private final int capacity;
        private final Map<Integer, Integer> cache;
        
        public LRUCache(int capacity) {
            this.capacity = capacity;
            this.cache = new LinkedHashMap<Integer, Integer>(capacity, 0.75f, true) {

				private static final long serialVersionUID = 1L;

				@Override
                protected boolean removeEldestEntry(Map.Entry<Integer, Integer> eldest) {
                    return size() > capacity;
                }
            };
        }
        
        public int get(int key) {
            return cache.getOrDefault(key, -1);
        }
        
        public void put(int key, int value) {
            cache.put(key, value);
        }
        
        public void printCache() {
            System.out.println("캐시 상태: " + cache);
        }
    }
    
    public static void problem6() {
        System.out.println("\n=== 문제 6: LRU 캐시 ===");
        LRUCache lru = new LRUCache(3);
        
        lru.put(1, 10);
        lru.put(2, 20);
        lru.put(3, 30);
        lru.printCache();
        
        System.out.println("get(2): " + lru.get(2));
        lru.printCache();
        
        lru.put(4, 40); // 1이 제거됨
        lru.printCache();
        
        System.out.println("get(1): " + lru.get(1)); // -1 반환
    }
    
    // 문제 7: 해시테이블을 이용한 Two Sum 문제
    public static void problem7() {
        System.out.println("\n=== 문제 7: Two Sum 문제 ===");
        int[] nums = {2, 7, 11, 15};
        int target = 9;
        
        Map<Integer, Integer> numMap = new HashMap<>();
        
        for (int i = 0; i < nums.length; i++) {
            int complement = target - nums[i];
            if (numMap.containsKey(complement)) {
                System.out.println("배열: " + Arrays.toString(nums));
                System.out.println("목표값: " + target);
                System.out.println("결과: [" + numMap.get(complement) + ", " + i + "]");
                System.out.println("값: [" + complement + ", " + nums[i] + "]");
                return;
            }
            numMap.put(nums[i], i);
        }
        
        System.out.println("해당하는 두 수를 찾을 수 없습니다.");
    }
    
    // 문제 8: 집합을 이용한 부분 문자열 중복 검사
    public static void problem8() {
        System.out.println("\n=== 문제 8: 부분 문자열 중복 검사 ===");
        String s = "abcabcbb";
        int k = 3; // 길이 3인 부분 문자열 검사
        
        Set<String> seen = new HashSet<>();
        Set<String> duplicates = new HashSet<>();
        
        for (int i = 0; i <= s.length() - k; i++) {
            String substring = s.substring(i, i + k);
            if (seen.contains(substring)) {
                duplicates.add(substring);
            } else {
                seen.add(substring);
            }
        }
        
        System.out.println("문자열: " + s);
        System.out.println("길이 " + k + "인 부분 문자열들: " + seen);
        System.out.println("중복된 부분 문자열: " + duplicates);
    }
    
    public static void main(String[] args) {
        System.out.println("=" .repeat(40));
        
        problem1(); // 중복 제거 및 정렬
        problem2(); // 문자 빈도수 계산
        problem3(); // 교집합 구하기
        problem4(); // 학생 성적 관리
        problem5(); // 그룹 애너그램
        problem6(); // LRU 캐시
        problem7(); // Two Sum
        problem8(); // 부분 문자열 중복 검사
        
        System.out.println("\n모든 문제가 완료되었습니다!");
    }
}