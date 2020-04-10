package daos;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.ignoreStubs;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.UserTransaction;

import org.jboss.logging.Logger;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import domain.TestEntity;

public class BaseDAOTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Mock
    private EntityManager em;

    @Mock
    private UserTransaction transaction;

    @Mock
    private TypedQuery<TestEntity> query;

    @Mock
    private Logger logger;

    @InjectMocks
    private TestDAO dao;

    @Mock
    private RuntimeException testException;

    @Test
    public void testFindAllWhenDataBaseIsEmptyShouldReturnEmptyList() {
        // setup
        when(em.createQuery(anyString(), eq(TestEntity.class))).thenReturn(
                query);
        when(query.getResultList()).thenReturn(new ArrayList<>());

        // verify
        assertThat(dao.findAll()).isEmpty();
        verify(em).createQuery("from domain.TestEntity", TestEntity.class);
    }

    @Test
    public void testFindAllWhenDataBaseIsNotEmptyShouldReturnListWithEntities() {
        // setup
        TestEntity testEntity1 = new TestEntity();
        TestEntity testEntity2 = new TestEntity();
        when(em.createQuery(anyString(), eq(TestEntity.class))).thenReturn(
                query);
        when(query.getResultList()).thenReturn(
                Arrays.asList(testEntity1, testEntity2));

        // verify
        assertThat(dao.findAll()).containsExactly(testEntity1, testEntity2);
        verify(em).createQuery("from domain.TestEntity", TestEntity.class);
    }

    @Test
    public void testFindByIdWhenEntityDoesNotExistShouldReturnNull() {
        // setup
        when(em.find(eq(TestEntity.class), any(Long.class))).thenReturn(null);

        // verify
        assertThat(dao.findById(1L)).isNull();
    }

    @Test
    public void testFindByIdWhenEntityExistsShouldReturnEntity() {
        // setup
        TestEntity testEntity = new TestEntity();
        when(em.find(eq(TestEntity.class), any(Long.class))).thenReturn(
                testEntity);

        // verify
        assertThat(dao.findById(1L)).isEqualTo(testEntity);
    }

    @Test
    public void testSaveWhenNoErrorOccursShouldReturnTrue() {
        // setup
        TestEntity testEntity = new TestEntity();
        doNothing().when(em).persist(any(TestEntity.class));

        // verify
        assertThat(dao.save(testEntity)).isTrue();
    }

    @Test
    public void testSaveWhenExceptionIsThrownShouldReturnFalse() {
        // setup
        TestEntity testEntity = new TestEntity();
        doThrow(testException).when(em).persist(any(TestEntity.class));

        // verify
        assertThat(dao.save(testEntity)).isFalse();
    }

    @Test
    public void testDeleteWhenEmContainsEntityAndNoErrorOccursShouldReturnTrue() {
        // setup
        TestEntity testEntity = new TestEntity();
        when(em.contains(any(TestEntity.class))).thenReturn(true);
        doNothing().when(em).remove(any(TestEntity.class));

        // verify
        assertThat(dao.delete(testEntity)).isTrue();
        verify(em).remove(testEntity);
        verifyNoMoreInteractions(ignoreStubs(em));
    }

    @Test
    public void testDeleteWhenEmDoesNotContainEntityAndNoErrorOccursShouldCallMergeAndReturnTrue() {
        // setup
        TestEntity testEntity = new TestEntity();
        when(em.contains(any(TestEntity.class))).thenReturn(false);
        when(em.merge(any(TestEntity.class))).thenReturn(testEntity);
        doNothing().when(em).remove(any(TestEntity.class));

        // verify
        assertThat(dao.delete(testEntity)).isTrue();
        InOrder inOrder = inOrder(em);
        inOrder.verify(em).merge(testEntity);
        inOrder.verify(em).remove(testEntity);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testDeleteWhenExceptionIsThrownInRemoveShouldReturnFalse() {
        // setup
        TestEntity testEntity = new TestEntity();
        when(em.contains(any(TestEntity.class))).thenReturn(true);
        doThrow(testException).when(em).remove(any(TestEntity.class));

        // verify
        assertThat(dao.delete(testEntity)).isFalse();
        verifyNoMoreInteractions(ignoreStubs(em));
    }

    @Test
    public void testDeleteWhenExceptionIsThrownInMergeShouldReturnFalse() {
        // setup
        TestEntity testEntity = new TestEntity();
        when(em.contains(any(TestEntity.class))).thenReturn(false);
        when(em.merge(any(TestEntity.class))).thenThrow(testException);

        // verify
        assertThat(dao.delete(testEntity)).isFalse();
        verifyNoMoreInteractions(ignoreStubs(em));
    }

    @Test
    public void testMergeWhenNoErrorOccursShouldReturnTrue() {
        // setup
        TestEntity testEntity = new TestEntity();
        when(em.merge(any(TestEntity.class))).thenReturn(testEntity);

        // verify
        assertThat(dao.merge(testEntity)).isTrue();
    }

    @Test
    public void testMergeWhenExceptionIsThrownShouldReturnFalse() {
        // setup
        TestEntity testEntity = new TestEntity();
        when(em.merge(any(TestEntity.class))).thenThrow(testException);

        // verify
        assertThat(dao.merge(testEntity)).isFalse();
    }

}
