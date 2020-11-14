package io.github.fukkitmc.fukkit;

import java.math.BigDecimal;

public final class PhysicalConstant {

    public static final PhysicalConstant UP_QUARK_MASS = ofError("2.05e-22", "6.5e-23");
    public static final PhysicalConstant DOWN_QUARK_MASS = ofError("2.05e-22", "6.5e-23");
    public static final PhysicalConstant CHARM_QUARK_MASS = of("1.04e-19");
    public static final PhysicalConstant STRANGE_QUARK_MASS = of("8.27e-21");
    public static final PhysicalConstant TOP_QUARK_MASS = of("1.41e-17");
    public static final PhysicalConstant BOTTOM_QUARK_MASS = of("3.43e-19");

    public static final PhysicalConstant CKM_12 = of("0.23");
    public static final PhysicalConstant CKM_23 = of("0.042");
    public static final PhysicalConstant CKM_13 = of("0.0035");
    public static final PhysicalConstant CKM_CP_VIOLATING_PHASE = of("0.995");

    public static final PhysicalConstant ELECTRON_MASS = of("4.18546e-23");
    public static final PhysicalConstant ELECTRON_NEUTRINO_MASS = of("1.6e-28"); // Or below
    public static final PhysicalConstant MUON_MASS = of("8.65418e-21");
    public static final PhysicalConstant MUON_NEUTRINO_MASS = of("1.6e-28"); // Or below
    public static final PhysicalConstant TAU_MASS = of("1.45535e-19");
    public static final PhysicalConstant TAU_NEUTRINO_MASS = of("1.6e-28"); // Or below

    public static final PhysicalConstant PMNS_12 = ofError("0.5973", "0.0175");
    public static final PhysicalConstant PMNS_23 = ofError("0.785", "0.12");
    public static final PhysicalConstant PMNS_13 = of("0.077"); // Approximately
    public static final PhysicalConstant PMNS_CP_VIOLATING_PHASE = null; // Unknown

    public static final PhysicalConstant FINE_STRUCTURE_CONSTANT = of("0.00729735");
    public static final PhysicalConstant STRONG_COUPLING_CONSTANT = of("1"); // Approximately

    public static final PhysicalConstant W_BOSON_MASS = ofError("6.5841e-18", "0.0012e-18");
    public static final PhysicalConstant Z_BOSON_MASS = ofError("7.46888e-18", "0.00016e-18");
    public static final PhysicalConstant HIGGS_BOSON_MASS = of("1.02e-17"); // Approximately

    public final BigDecimal value;
    public final BigDecimal error;

    public PhysicalConstant(BigDecimal value, BigDecimal error) {
        this.value = value;
        this.error = error;
    }

    private static PhysicalConstant of(String value) {
        return new PhysicalConstant(new BigDecimal(value), BigDecimal.ZERO);
    }

    private static PhysicalConstant ofError(String value, String error) {
        return new PhysicalConstant(new BigDecimal(value), new BigDecimal(error));
    }
}
