package daos;

import javax.enterprise.context.RequestScoped;

import domain.Structure;

@RequestScoped
public class StructureDAO extends BaseDAO<Structure> {

    public StructureDAO() {
        super(Structure.class);
    }

}
