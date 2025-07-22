**1. 개념정리**

  ㄴ 쿠버네티스가 기동하기 위한 아키텍처는 무엇인지? 쿠버네티스가 관리하는 resource들은 무엇인가?

 

**2. Udemy 실습 문제 정리**

  ㄴ 자꾸 까먹고, 시험 전에 섬머리 형태로 기억하여 공부 효율성을 높이려 했음.

 

**3. 기출문제 정리**

  ㄴ CKA는 주관식이기 때문에 기출문제를 찾아볼 생각을 하지 못했다.

  ㄴ **CKA 기출문제로 검색해보니,
     시험 후기들의 기출문제가 생각보다 많고,
     그리고 중요한 것은 기출문제가 거의 똑같았다.**

### [시험 문제](https://nayoungs.tistory.com/entry/CKACertified-Kubernetes-Administrator-%ED%95%A9%EA%B2%A9-%EB%B0%8F-%EC%8B%9C%ED%97%98-%ED%9B%84%EA%B8%B0#%EC%-B%-C%ED%--%--%--%EB%AC%B-%EC%A-%-C)

기억나는 대로 작성해보면 다음과 같다.

1. 특정 Node가 Not Ready 상태이고, Ready 상태가 되도록 TroubleShooting하기

```properties
#고득점 문제이니, 반드시 맞추자.
ssh node01
systemctl status kubelet
systemctl restart kubelet
```

2. Cluster Upgrade 하라. 이때 Controlplane Node만 업그레이드를 진행하기
3. ETCD Snapshot Save & Restore
4. PVC 생성 후 Pod와 PVC를 연동(mount)하기
5. Log를 저장하는 Sidecar 컨테이너를 추가하여, Multi Container로 구성하기
6. 특정 노드에 Pod가 배포되도록 하기 : nodeSelector, nodeName 모두 가능하다.
7. nginx:1.16 이미지로 Deployment 생성 후, nginx:1.17로 업그레이드

```routeros
kubectl set image deployment test-depoy nginx=nginx:1.17
```

8. Networkpolicy를 생성해서, 특정 Namespace의 Pod만 특정 Pod로의 Ingress를 허용하기
9. ServiceAccount, Role, Rolebinding 생성하기
10. 특정 Node를 drain해서 Pod를 다른 노드로 옮기고 SchedulingDisabled 상태로 만들기
11. Pod에서 ‘File Not Found’ log를 grep로 추출해서 파일로 저장하기
12. CPU 사용률이 가장 높은 Pod를 특정 label로 조회해서 파일로 저장하기

 



 

### ㅁ 시험 시 팁

 ㅇ 명령어 타이프 시간을 줄이기 위해 줄임 명령어를 사용함

```
# (기본설정) kubectl을 k로 줄여주어서 타자 시간을 멀어줌. 
alias k=kubectl                         # 시험환경에 이미 설정됨.

# (설정필요) sample yaml 얻거나 yaml 문법 정상 테스트 시 자주 사용
export do="--dry-run=client -o yaml"    # k create deploy nginx --image=nginx $do

# (설정필요) pod 삭제 시 즉시 수행됨
export now="--force --grace-period 0"   # k delete pod x $now
```

 ㅇ pod가 삭제될 때에 graceful shutdown 정책이 기본 정해져, container의 프로세스 종료를 위한 지연이 발생한다.

 ㅇ kill -15면 어플리케이션 종료 명령어이듯, 위의 force delete는 kill -9 즉시 종료에 해당하는 방법이다.

 

### ㅁ  Cluster 관련

**ㅇ Cluster Upgrade (Controlplane Node만 진행)**

 ㄴ [[CKA\] Udemy 실습문제풀이 - Cluster Maintenance](https://peterica.tistory.com/524)

```
# master upgrade
1. master node drain
2. master node의 kubeadm upgrade 
3. master node의 kubelet, kubectl upgrade 및 daemon restart
4. master node의 uncordon

# work upgrade
1. work node drain
2. work node ssh 접속
3. work node의 kubeadm upgrade 
3. work node의 kubelet, kubectl upgrade 및 daemon restart
4. work node의 uncordon
```

**ㅇ ETCD snapshot save & restore**

 ㄴ 빠지지 않고 나오는 고득점 문제.

 ㄴ 공식문서에서 명령어 복사 후 문제에 주어진 옵션 값 복붙으로 명령문 완성 후 실행

 ㄴ [[CKA\] 기출문제 - ETCD Backup and Restore](https://peterica.tistory.com/460)

 

### ㅁ 트러블 슈팅 관련

**ㅇ 특정 Node가 NotReady 상태인데 Ready가 되도록 TroubleShooting(\**고배점 문제\**)**

```
# 문제 노드 확인
$ k get no 

# ssh 접속
ssh wk8s-node-0

# 루트 권한 설정
$ sudo -i

# kubelet 서비스 상태 확인
$ systemctl status kubelet
... inactive 상태 확인

# 재기동
$ systemctl restart kubelet 

# 상태확인
$ systemctl status kubelet
... active 상태 확인
```

 

### ㅁ Resource 관련

**ㅇ PVC 생성 후 Pod와 PVC 연동 (PV는 이미 존재), PVC 용량 수정**

 **ㄴ 스토리지 클래스의 allowVolumeExpansion 필드가 true로 설정된 경우에만 PVC를 확장할 수 있다.**

 **ㄴ 공식문서:**

  **- \**\*\*[스토리지로 퍼시스턴트볼륨(PersistentVolume)을 사용하도록 파드 설정하기](https://kubernetes.io/ko/docs/tasks/configure-pod-container/configure-persistent-volume-storage/)\*\**\***

  ***\*-\**** [**퍼시스턴트 볼륨 클레임 확장**](https://kubernetes.io/ko/docs/concepts/storage/persistent-volumes/#퍼시스턴트-볼륨-클레임-확장)

```
# 문제에서 storageclasses를 지정해 주지 않으면 기본을 사용한다.
$ k get sc
NAME                 PROVISIONER                RECLAIMPOLICY   VOLUMEBINDINGMODE   ALLOWVOLUMEEXPANSION   AGE
standard (default)   k8s.io/minikube-hostpath   Delete          Immediate           true                   17d

# 확장가능 확인, AllowVolumeExpansion:  True
$  k describe sc standard
Name:            standard
IsDefaultClass:  Yes
Annotations:     kubectl.kubernetes.io/last-applied-configuration={"apiVersion":"storage.k8s.io/v1","kind":"StorageClass","metadata":{"annotations":{"storageclass.kubernetes.io/is-default-class":"true"},"labels":{"addonmanager.kubernetes.io/mode":"EnsureExists"},"name":"standard"},"provisioner":"k8s.io/minikube-hostpath"}
,storageclass.kubernetes.io/is-default-class=true
Provisioner:           k8s.io/minikube-hostpath
Parameters:            <none>
MountOptions:          <none>
ReclaimPolicy:         Delete
VolumeBindingMode:     Immediate
Events:                <none>

# 기본 storageClass 수정
# allowVolumeExpansion: true  
$ k edit sc standard
..............
allowVolumeExpansion: true <=== 추가

# PV 생성, 공식문서 참조(pods/storage/pv-volume.yaml)
apiVersion: v1
kind: PersistentVolume
metadata:
  name: task-pv-volume
  labels:
    type: local
spec:
  storageClassName: standard <=== 기본으로 수정
  capacity:
    storage: 10Mi
  accessModes:
    - ReadWriteOnce
  hostPath:
    path: "/mnt/data"
    
$ k apply -f pv.yaml
persistentvolume/task-pv-volume created

# PVC 생성, 공식문서 참조(pods/storage/pv-claim.yaml)
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: task-pv-claim
spec:
  storageClassName: standard <=== 기본으로 수정
  accessModes:
    - ReadWriteOnce
  resources:
    requests:
      storage: 10Mi <== 수정함

$ k apply -f pvc.yaml
persistentvolumeclaim/task-pv-claim created

# 확인
$ k get pv,pvc
NAME                              CAPACITY   ACCESS MODES   RECLAIM POLICY   STATUS   CLAIM                   STORAGECLASS   REASON   AGE
persistentvolume/task-pv-volume   10Mi       RWO            Retain           Bound    default/task-pv-claim   manual                  30s

NAME                                  STATUS   VOLUME           CAPACITY   ACCESS MODES   STORAGECLASS   AGE
persistentvolumeclaim/task-pv-claim   Bound    task-pv-volume   10Mi       RWO            manual         26s

# POD 생성, 공식문서 참조(pods/storage/pv-pod.yaml)
apiVersion: v1
kind: Pod
metadata:
  name: task-pv-pod
spec:
  volumes:
    - name: task-pv-storage
      persistentVolumeClaim:
        claimName: task-pv-claim
  containers:
    - name: task-pv-container
      image: nginx
      ports:
        - containerPort: 80
          name: "http-server"
      volumeMounts:
        - mountPath: "/usr/share/nginx/html"
          name: task-pv-storage

$ k apply -f pv-pod.yaml
pod/task-pv-pod created

# pvc 10Mi -> 90Mi 용량 변경
$ k edit pvc task-pv-claim
...........
spec:
  accessModes:
  - ReadWriteOnce
  resources:
    requests:
      storage: 90Mi  <== 변경
  storageClassName: manual
  volumeMode: Filesystem
  volumeName: task-pv-volume
...........
persistentvolumeclaim/task-pv-claim edited


# 변경 이벤트 확인
#
$ k describe pvc task-pv-claim
..............
  Warning  ExternalExpanding      3m11s  volume_expand                                                           waiting for an external controller to expand this PVC
```


**ㅇ sidecar multi containers로 sidecar의 log 확인하기**

 **ㄴ 기존 container에 volume emptyDir 설정하여 /var/log를 공유하여 sidecar로 로그 확인**

 **ㄴ 공식문서: [로깅 에이전트가 있는 사이드카 컨테이너](https://kubernetes.io/ko/docs/concepts/cluster-administration/logging/#로깅-에이전트가-있는-사이드카-컨테이너)**

 ㄴ 동영상: [[따배씨\] 05. Side-car Container Pod 실행하기](https://www.youtube.com/watch?v=uACioswfZI8&list=PLApuRlvrZKojqx9-wIvWP3MPtgy2B372f&index=8)

```
# 테스트를 위한 사전 pod.yaml 작성 및 실행
apiVersion: v1
kind: Pod
metadata:
  name: eshop-cart-app
spec:
  containers:
  - image: busybox
    name: cart-app
    command:
    - /bin/sh
    - -c    
    - 'i=1;while :;do  echo -e "$i: Price: $((RANDOM % 10000 + 1))" >> /var/log/cart-app.log;
      i=$((i+1)); sleep 2; done'
    volumeMounts:
    - name: varlog
      mountPath: /var/log
  volumes:
  - emptyDir: {}
    name: varlog

# 로그확인 시 로그가 보이지 않는다.
$ k logs eshop-cart-app

# 공식문서  > 로깅 에이전트와 함께 사이드카 컨테이너 사용 
   > admin/logging/two-files-counter-pod-streaming-sidecar.yaml에서 복사
.......
  - name: count-log-1
    image: busybox:1.28
    args: [/bin/sh, -c, 'tail -n+1 -F /var/log/1.log']
    volumeMounts:
    - name: varlog
      mountPath: /var/log
.......

# 시험환경에서
# 기존 콘테이너에서 yaml 복사, 문제번호+log.yaml
$ k get po eshop-cart-app -o yaml > 9-log.yaml

apiVersion: v1
kind: Pod
metadata:
  name: eshop-cart-app
spec:
  containers:
  - image: busybox
    name: cart-app
    command:
    - /bin/sh
    - -c    
    - 'i=1;while :;do  echo -e "$i: Price: $((RANDOM % 10000 + 1))" >> /var/log/cart-app.log;
      i=$((i+1)); sleep 2; done'
    volumeMounts:
    - name: varlog
      mountPath: /var/log
  - name: count-log-1      
    image: busybox:1.28
    args: [/bin/sh, -c, 'tail -n+1 -F /var/log/cart-app.log'] 
    volumeMounts:
    - name: varlog
      mountPath: /var/log
  volumes:
  - emptyDir: {}
    name: varlog


# yaml 검증
$ k apply -f 9-log.yaml --dry-run=server
The Pod "eshop-cart-app" is invalid: spec.containers: Forbidden: pod updates may not add or remove containers
... 문법오류가 발생하지 않으면 pod container의 추가 수정은 불가 경고가 뜬다.

# 적용을 위한 기존 pod 제거
# delete 시 시간이 소요되어 즉시 삭제 옵션 추가 --force --grace-period 0 
$ k delete po eshop-cart-app --force --grace-period 0

# 적용
$ k apply -f 9-log.yaml

# 로그 확인
$ k logs eshop-cart-app count-log-1
1: Price: 9575
2: Price: 4341
3: Price: 406
4: Price: 8355
5: Price: 6885
6: Price: 4549
```

 

**ㅇ Pod에 nodeSelector (disktype=ssd) 추가**

```
# node-selector.yaml 생성
apiVersion: v1
kind: Pod
metadata:
  name: nginx
  labels:    
    env: test
spec:
  containers:
  - name: nginx
    image: nginx
  nodeSelector:    
    disktype: ssd

# apply
$ k apply -f node-selector.yaml

# 확인
k get po nginx -o wide
```


**ㅇ 이미지 nginx 1.16으로 Deployment 생성 후 이미지를 nginx 1.17로 업그레이드 하기**

```
# nginx 1.16 생성
$ k create deploy deploy01 --image nginx:1.16

# nginx 1.17 변경
$ k set image deployment/deploy01 nginx=nginx:1.17
```

 

### ㅁ 네트워크

**ㅇ Pod(port 80)생성하고 NodePort타입 Service 생성**

```
# pod 생성
$ k run nginx-resolver --image nginx 

# pod expose
$ k expose pod nginx-resolver -name nginx-resolver-service\
 --port 80 --target-port 80 --type NodePort
 
# 확인
$ k get svc nginx-resolver-service -o yaml
```


**ㅇ Ingress를 생성해서 이미 생성 되어 있는 서비스와 연결하고 확인**

 **ㄴ 테스트를 위해 외부 IP 주소를 노출하여 클러스터의 애플리케이션에 접속하기 기준으로 deployment와 service-LoadBalancer을 생성하였다.**

```
# deployment 생성 - containerPort: 8080
$ k apply -f https://k8s.io/examples/service/load-balancer-example.yaml

# service 생성
$ k expose deployment hello-world --type=LoadBalancer --name=my-service

# service 확인
$ k get svc my-service
NAME         TYPE           CLUSTER-IP      EXTERNAL-IP     PORT(S)          AGE
my-service   LoadBalancer   10.96.209.198   10.96.209.198   8080:31426/TCP   33m
```

 

 ㄴ 문제에서 /hi로 요청이 오면 기존 서비스로 연결



![img](https://blog.kakaocdn.net/dna/bO6WMX/btsEnYXH1gt/AAAAAAAAAAAAAAAAAAAAAGSqCLNiXY_4VcGWVg5vNECCRL4-Bn81pGs_Ho8BGiI4/img.png?credential=yqXZFxpELC7KVnFOS48ylbz2pIh7yKj8&expires=1753973999&allow_ip=&allow_referer=&signature=PyRoGDWrOeh1DRXfsOjOL0YJS4c%3D)



 ㄴ 인그레스 리소스에서 service/networking/minimal-ingress.yaml 샘플 복사

```
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: minimal-ingress
  annotations:
    nginx.ingress.kubernetes.io/rewrite-target: /
spec:
  ingressClassName: nginx-example <== 삭제
  rules:
  - http:
      paths:
      - path: /hi <== 수정
        pathType: Prefix
        backend:
          service:
            name: my-service <== 수정
            port:
              number: 80
              
# 적용
$  k apply -f minimal-ingress.yaml
ingress.networking.k8s.io/minimal-ingress created

# 확인
$ k describe ingress minimal-ingress
Name:             minimal-ingress
Labels:           <none>
Namespace:        default
Address:
Default backend:  <default>
Rules:
  Host        Path  Backends
  ----        ----  --------
  *
              /hi   my-service:80 (10.244.0.154:8080,10.244.0.155:8080,10.244.1.17:8080 + 2 more...)
```

 


**ㅇ Networkpolicy를 생성해서 특정 namespace의 Pod만 특정 경로로 연결**

ㄴ 참고 동영상: [[따배씨\] 30.Network Policy](https://www.youtube.com/watch?v=hvhqrzoTfIY&list=PLApuRlvrZKojqx9-wIvWP3MPtgy2B372f&index=35)
ㄴ 공식문서 검색: [The NetworkPolicy resource](https://kubernetes.io/docs/concepts/services-networking/network-policies/#networkpolicy-resource)

```
# 공식문서에서 예문 복사
apiVersion: networking.k8s.io/v1
kind: NetworkPolicy
metadata:
  name: test-network-policy <== 변경
  namespace: default <== 변경
spec:
  podSelector:
    matchLabels:
      role: db
  policyTypes:
  - Ingress
  ingress:
  - from:
    - namespaceSelector:
        matchLabels:
          project: myproject <== 변경
    - podSelector:
        matchLabels:
          role: frontend <== 변경
    ports:
    - protocol: TCP
      port: 80
      
# 적용
$ k apply -f ploicy.yaml

# 확인
$ k describe networkpolicy -n default test-network-policy
...PodSelector, To Port, From NamespaceSelector 체크
```

 

 

### ㅁ 권한 관련

**ㅇ ServiceAccount 생성, Role 생성, Role Binding 생성 후 확인**

```
# serviceaccount 생성
$ k create serviceaccount john $do > sa.yaml

# apply
$ k apply -f sa.yaml

# role 생성
$ k -n development create role developer --resource=pods --verb=create 
$ k -n development create rolebinding developer-role-binding \
  --role=developer --user=john
```

 

### ㅁ 스케줄 관련

ㅇ 특정 **Node를 drain하여 해당 노드는 SchedulingDisabled상태로 변경하고 Pod를 다른 Node로 옮기기**

```
# 특정노드 이름 조회
$ k get no --show-labels | grep k8s-node-0

# # node drain
# DaemonSet에서 관리하는 포드가 있는 경우 
# 노드를 성공적으로 비우려면 --ignore-daemonsetswith를 지정해야한다.
$ k drain --ignore-daemonsets k8s-node-0

# node 상태 확인
$ k get no -o wide

# pod의 위치확인
$ k get po -o wide
```


ㅇ **특정 Deployment에 대해 replicas 수정**

 ㄴ 공식문서: [디플로이먼트 스케일링](https://kubernetes.io/ko/docs/concepts/workloads/controllers/deployment/#디플로이먼트-스케일링)

```
$ k scale deployment/nginx-deployment --replicas=10
```

 

### ㅁ 필터를 통한 데이터 추출

**ㅇ Pod에서 log grep해서 파일로 추출**

```
# ErrorMessage 로그필터
$ k logs podName | grep ErrorMessage > 답안경로
```


**ㅇ Taint가 없는 Node의 개수를 파일로 저장**

```
# 노드 확인
$ k get no => 노드 갯수 3개 확인

# k describe no | grep -i taint
Taints:             <none>  <== 없는 것은 none으로 확인

# 답안작성
echo '2' > 답안파일 경로
```

 

**ㅇ Node의 상태가 ready 개수를 파일로 저장**

 **ㄴ 이런 간단한 문제들은 [kubectl Quick Reference](https://kubernetes.io/docs/reference/kubectl/quick-reference/)에서 검색을 해봅니다.**

 **ㄴ ready로 웹 검색을 하면 다음과 같은 방법 확인**

```
# Check which nodes are ready
JSONPATH='{range .items[*]}{@.metadata.name}:{range @.status.conditions[*]}{@.type}={@.status};{end}{end}' \
 && kubectl get nodes -o jsonpath="$JSONPATH" | grep "Ready=True"

# Check which nodes are ready with custom-columns
kubectl get node -o custom-columns='NODE_NAME:.metadata.name,STATUS:.status.conditions[?(@.type=="Ready")].status'
```

 ㄴ 응용하여 완성

```
$ k get node -o custom-columns='NODE_NAME:.metadata.name,STATUS:.status.conditions[?(@.type=="Ready")].status' --no-headers| wc -l > 파일명
```


**ㅇ 사용률이 가장 높은 Pod를 특정 label로만 조회해서 파일로 저장**

```
# 특정 namespace에 control-plane(샘플)라벨로 필터하여 cpu로 정렬
k top po -n kube-system -l tier=control-plane --sort-by=cpu

# 답안작성
$ echo '노드이름' > 답변 파일 위치
```

 

 