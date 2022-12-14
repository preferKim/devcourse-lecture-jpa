package devcourse.jpa.domain.order;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import java.time.LocalDateTime;
import java.util.UUID;

import static devcourse.jpa.domain.order.OrderStatus.OPENED;

@Slf4j
@SpringBootTest
class OrderPersistenceTest {

    @Autowired
    EntityManagerFactory emf;

    @Test
    void member_insert() {
        Member member = new Member();
        member.setName("kimseonho");
        member.setAddress("서울시 동작구(만) 움직이면 쏜다.");
        member.setAge(27);
        member.setNickName("Heman");
        member.setDescription("3기 백둥이여라~");

        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();

        em.persist(member);

        transaction.commit();
    }

    @Test
    void 잘못된_설계() {
        Member member = new Member();
        member.setName("kanghonggu");
        member.setAddress("서울시 동작구(만) 움직이면 쏜다.");
        member.setAge(33);
        member.setNickName("guppy.kang");
        member.setDescription("백앤드 개발자에요.");

        EntityManager entityManager = emf.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        entityManager.persist(member);
        Member memberEntity = entityManager.find(Member.class, 1L); // 영속화된 회원

        Order order = new Order();
        order.setUuid(UUID.randomUUID().toString());
        order.setOrderDateTime(LocalDateTime.now());
        order.setOrderStatus(OPENED);
        order.setMemo("부재시 전화주세요.");
        order.setMemberId(memberEntity.getId()); // 외래키를 직접 지정

        entityManager.persist(order);
        transaction.commit();

        Order orderEntity = entityManager.find(Order.class, order.getUuid()); // SELECT Order
        // FK 를 이용해 회원 다시 조회
        Member orderMemberEntity = entityManager.find(Member.class, orderEntity.getMemberId()); // SELECT Member
        // orderEntity.getMember()f // 객체중심 설계라면 객체그래프 탐색을 해야하지 않을까?
        log.info("nick : {}", orderMemberEntity.getNickName());
    }

    @Test
    @DisplayName("연관관계 테스트")
    void related_mapping() {
        EntityManager entityManager = emf.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        transaction.begin();

        Member member = new Member();
        member.setName("kanghonggu");
        member.setAddress("서울시 동작구(만) 움직이면 쏜다.");
        member.setAge(33);
        member.setNickName("guppy.kang");
        member.setDescription("백앤드 개발자에요.");

        entityManager.persist(member);

        Order order = new Order();
        order.setUuid(UUID.randomUUID().toString());
        order.setOrderStatus(OPENED);
        order.setOrderDateTime(LocalDateTime.now());
        order.setMemo("부재 시 연락 주세요");
        order.setMember(member);

        entityManager.persist(order);
        transaction.commit();

        entityManager.clear();

        Order entity = entityManager.find(Order.class, order.getUuid());

        log.info("{}", entity.getMember().getNickName()); // 객체 그래프 탐색
        log.info("{}", entity.getMember().getOrders().size()); // 객체 그래프 탐색
        log.info("{}", order.getMember().getOrders().size()); // 객체 그래프 탐색
    }
}