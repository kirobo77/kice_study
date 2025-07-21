import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Cloud/On-Premise 환경에서 요구되는 핵심 알고리즘 구현
 * 
 * 분산 캐싱 알고리즘 (Consistent Hashing)
 * 해시 공간 (Hash Space)
 *   일반적으로 0 ~ 2³²-1의 정수 범위를 사용하는 원형 공간.
 * 해시 링 (Hash Ring)
 *   해시 공간을 원형 구조로 연결한 것. 맨 끝과 처음이 연결되어 순환 구조를 이룸.
 * 키-서버 매핑
 *   키와 서버 모두 해시 함수를 통해 링에 위치시킨다.
 *   키는 자신보다 해시값이 큰 첫 번째 서버에 매핑된다 (시계 방향 탐색).
 * 
 */
public class ConsistentHashingSample {

	// ===== 분산 캐싱 알고리즘 (Consistent Hashing) =====

	/**
	 * 일관된 해싱을 구현하는 클래스
	 */
	static class ConsistentHashRing {
		private TreeMap<Integer, String> ring;
		private Set<String> nodes;
		private int virtualNodeCount;

		public ConsistentHashRing(int virtualNodeCount) {
			this.ring = new TreeMap<>();
			this.nodes = new HashSet<>();
			this.virtualNodeCount = virtualNodeCount;
		}

		/**
		 * 노드를 링에 추가하는 메소드
		 */
		public void addNode(String node) {
			nodes.add(node);
			int hash = calculateHash(node);
			ring.put(hash, node);
		}

		/**
		 * 노드를 링에서 제거하는 메소드
		 */
		public void removeNode(String node) {
			nodes.remove(node);
			int hash = calculateHash(node);
			ring.put(hash, node);
			ring.remove(hash);
		}

		/**
		 * 키에 대한 노드를 찾는 메소드
		 */
		public String getNode(String key) {
			if (ring.isEmpty())
				return null;

			int hash = calculateHash(key);

			// 해시보다 큰 첫 번째 노드를 찾음
			Map.Entry<Integer, String> entry = ring.ceilingEntry(hash);

			// 찾지 못하면 링의 첫 번째 노드를 반환 (순환 구조)
			if (entry == null) {
				entry = ring.firstEntry();
			}

			return entry.getValue();
		}

		/**
		 * 해시 함수 (간단한 해시 구현)
		 */
		private int calculateHash(String input) {
			return input.hashCode() % virtualNodeCount;
		}

		/**
		 * 현재 링 상태를 출력하는 메소드
		 */
		public void printRing() {
			System.out.println("=== Consistent Hash Ring ===");
			for (Map.Entry<Integer, String> entry : ring.entrySet()) {
				System.out.println("Hash: " + entry.getKey() + " -> Node: " + entry.getValue());
			}
		}
	}

	/**
	 * 일관된 해싱 테스트
	 */
	public static void testConsistentHashing() {
		System.out.println("========== Consistent Hashing Test ==========");

		ConsistentHashRing ring = new ConsistentHashRing(3);

		// 노드 추가
		ring.addNode("node1");
		ring.addNode("node2");
		ring.addNode("node3");

		// 키 분배 테스트
		String[] keys = { "user1", "user2", "user3", "user4", "user5", "data1", "data2", "data3" };

		System.out.println("--- Key Distribution ---");
		for (String key : keys) {
			String node = ring.getNode(key);
			System.out.println("Key: " + key + " -> Node: " + node);
		}

		// 노드 제거 후 테스트
		System.out.println("--- After removing node2 ---");
		ring.removeNode("node2");
		for (String key : keys) {
			String node = ring.getNode(key);
			System.out.println("Key: " + key + " -> Node: " + node);
		}

		System.out.println();
	}

	/**
	 * 메인 메소드 - 테스트 실행
	 */
	public static void main(String[] args) {
		System.out.println("일관된 해싱 테스트 테스트 시작");
		System.out.println("=".repeat(40));

		try {
			// 일관된 해싱 테스트
			testConsistentHashing();

		} catch (Exception e) {
			System.err.println("테스트 중 오류 발생: " + e.getMessage());
			e.printStackTrace();
		}

		System.out.println("일관된 해싱 테스트 테스트 완료");
	}

}
