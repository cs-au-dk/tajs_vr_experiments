package dk.brics.tajsvr;

import dk.brics.tajs.lattice.Value;
import edu.colorado.plv.js_value_refiner.API;
import edu.colorado.plv.js_value_refiner.RefinementConstraint;
import forwards_backwards_api.Assumption;
import forwards_backwards_api.Forwards;
import forwards_backwards_api.ProgramLocation;
import forwards_backwards_api.RefineResult;
import forwards_backwards_api.Refiner;
import forwards_backwards_api.RefutationResult;
import forwards_backwards_api.memory.MemoryLocation;

public class ValueRefiner implements Refiner<RefinementConstraint> {

    @Override
    public RefineResult refine(ProgramLocation location, MemoryLocation reg, RefinementConstraint constraint, Forwards forwards) {
        return API.refine(location, reg, constraint, forwards);
    }

    @Override
    public RefutationResult refute(ProgramLocation location, RefinementConstraint formula, Forwards forwards) {
        return API.refute(location, formula, forwards);
    }

    @Override
    public RefinementConstraint mkEqualityConstraint(MemoryLocation reg, Value value) {
        return API.mkEqualityConstraint(reg, value);
    }

    @Override
    public RefinementConstraint mkInequalityConstraint(MemoryLocation reg, Value value) {
        return API.mkInequalityConstraint(reg, value);
    }

    @Override
    public RefinementConstraint mkConjunctionConstraint(RefinementConstraint f1, RefinementConstraint f2) {
        return API.mkConjunctionConstraint(f1, f2);
    }

    @Override
    public RefinementConstraint mkTrueConstraint() {
        return API.mkTrueConstraint();
    }

    @Override
    public boolean assumptionHolds(ProgramLocation location, Assumption a, Forwards forwards) {
        return API.assumptionHolds(location, a, forwards);
    }
}
