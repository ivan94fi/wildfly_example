package daos;

import domain.TestEntity;

public class TestDAO extends BaseDAO<TestEntity> {
    // This class has the only purpose to represent a concrete class
    // to test BaseDAO functionality

    public TestDAO() {
        super(TestEntity.class);
    }
}
