package visad.data.visad.object;

import java.io.DataInput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.EOFException;

import visad.CoordinateSystem;
import visad.Data;
import visad.ErrorEstimate;
import visad.Real;
import visad.RealTuple;
import visad.RealTupleType;
import visad.SingletonSet;
import visad.Unit;
import visad.VisADException;

import visad.data.visad.BinaryObjectCache;
import visad.data.visad.BinaryReader;
import visad.data.visad.BinaryWriter;

public class BinarySingletonSet
  implements BinaryObject
{
  public static final int computeBytes(RealTupleType sampleType,
                                       Real[] sampleReals,
                                       CoordinateSystem cs, Unit[] units,
                                       ErrorEstimate[] errors)
  {
    final boolean trivialTuple = BinaryRealTuple.isTrivialTuple(sampleType,
                                                                sampleReals);

    final int unitsLen = BinaryUnit.computeBytes(units);
    final int errorsLen = BinaryErrorEstimate.computeBytes(errors);
    return 1 + 4 + 1 +
      1 + BinaryRealTuple.computeBytes(sampleReals, cs, trivialTuple) +
      (cs == null ? 0 : 5) +
      (unitsLen == 0 ? 0 : unitsLen + 1) +
      (errorsLen == 0 ? 0 : errorsLen + 1) +
      1;
  }

  public static final Real[] getSampleReals(RealTuple sample)
  {
    Data[] comps = sample.getComponents();
    if (comps == null) {
      return null;
    }

    Real[] sampleReals = new Real[comps.length];
    for (int i = 0; i < comps.length; i++) {
      sampleReals[i] = (Real )comps[i];
    }

    return sampleReals;
  }

  public static SingletonSet read(BinaryReader reader)
    throws IOException, VisADException
  {
    BinaryObjectCache cache = reader.getCoordinateSystemCache();
    DataInput file = reader.getInput();

    RealTuple sample = null;
    CoordinateSystem cs = null;
    Unit[] units = null;
    ErrorEstimate[] errs = null;

    boolean reading = true;
    while (reading) {
      final byte directive;
      try {
        directive = file.readByte();
      } catch (EOFException eofe) {
        return null;
      }

      switch (directive) {
      case FLD_SAMPLE:
        sample = (RealTuple )BinaryGeneric.read(reader);
        break;
      case FLD_INDEX_COORDSYS:
if(DEBUG_RD_DATA)System.err.println("rdSglSet: FLD_INDEX_COORDSYS (" + FLD_INDEX_COORDSYS + ")");
        final int index = file.readInt();
if(DEBUG_RD_DATA&&DEBUG_RD_CSYS)System.err.println("rdSglSet: cSys index (" + index + ")");
        cs = (CoordinateSystem )cache.get(index);
if(DEBUG_RD_DATA&&!DEBUG_RD_CSYS)System.err.println("rdSglSet: cSys index (" + index + "=" + cs + ")");
        break;
      case FLD_INDEX_UNITS:
if(DEBUG_RD_DATA)System.err.println("rdSglSet: FLD_INDEX_UNITS (" + FLD_INDEX_UNITS + ")");
        units = BinaryUnit.readList(reader);
        break;
      case FLD_INDEX_ERRORS:
if(DEBUG_RD_DATA)System.err.println("rdSglSet: FLD_INDEX_ERRORS (" + FLD_INDEX_ERRORS + ")");
        errs = BinaryErrorEstimate.readList(reader);
        break;
      case FLD_END:
if(DEBUG_RD_DATA)System.err.println("rdSglSet: FLD_END (" + FLD_END + ")");
        reading = false;
        break;
      default:
        throw new IOException("Unknown SingletonSet directive " +
                              directive);
      }
    }

    if (sample == null) {
      throw new IOException("No sample found for SingletonSet");
    }

    return new SingletonSet(sample, cs, units, errs);
  }

  public static final void writeDependentData(BinaryWriter writer,
                                              RealTuple sample,
                                              CoordinateSystem cs,
                                              Unit[] units,
                                              ErrorEstimate[] errors,
                                              SingletonSet set)
    throws IOException
  {
    if (!set.getClass().equals(SingletonSet.class)) {
      return;
    }

    if (cs != null) {
if(DEBUG_WR_DATA&&!DEBUG_WR_CSYS)System.err.println("wrSglSet: coordSys (" + cs + ")");
      BinaryCoordinateSystem.write(writer, cs, SAVE_DATA);
    }

    if (units != null) {
if(DEBUG_WR_DATA&&!DEBUG_WR_UNIT){
  System.err.println("wrSglSet: List of " + units.length + " Units");
  for(int x=0;x<units.length;x++){
    System.err.println("wrSglSet:    #"+x+": "+units[x]);
  }
}
      BinaryUnit.writeList(writer, units, SAVE_DATA);
    }

    if (errors != null) {
if(DEBUG_WR_DATA&&!DEBUG_WR_ERRE){
  System.err.println("wrSglSet: List of " + errors.length + " ErrorEstimates");
  for(int x=0;x<errors.length;x++){
    System.err.println("wrSglSet:    #"+x+": "+errors[x]);
  }
}
      BinaryErrorEstimate.writeList(writer, errors, SAVE_DATA);
    }

    BinaryGeneric.write(writer, sample, SAVE_DEPEND);
  }

  public static final void write(BinaryWriter writer, RealTuple sample,
                                 CoordinateSystem cs, Unit[] units,
                                 ErrorEstimate[] errors, SingletonSet set,
                                 Object token)
    throws IOException
  {
    writeDependentData(writer, sample, cs, units, errors, set);

    // if we only want to write dependent data, we're done
    if (token == SAVE_DEPEND) {
      return;
    }

    if (!set.getClass().equals(SingletonSet.class)) {
if(DEBUG_WR_DATA)System.err.println("wrSglSet: punt "+set.getClass().getName());
      BinaryUnknown.write(writer, set, token);
      return;
    }

    if (sample == null) {
      throw new IOException("Null SingletonSet sample");
    }

    int csIndex = -1;
    if (cs != null) {
      csIndex = writer.getCoordinateSystemCache().getIndex(cs);
      if (csIndex < 0) {
        throw new IOException("CoordinateSystem " + cs + " not cached");
      }
    }

    int[] unitsIndex = null;
    if (units != null) {
      unitsIndex = BinaryUnit.lookupList(writer.getUnitCache(), units);
    }

    int[] errorsIndex = null;
    if (errors != null) {
      errorsIndex = BinaryErrorEstimate.lookupList(writer.getErrorEstimateCache(),
                                                   errors);
    }

    RealTupleType sampleType = (RealTupleType )sample.getType();
    Real[] sampleReals = getSampleReals(sample);

    final int objLen = computeBytes(sampleType, sampleReals, cs, units,
                                    errors);

    DataOutputStream file = writer.getOutputStream();

if(DEBUG_WR_DATA)System.err.println("wrSglSet: OBJ_DATA (" + OBJ_DATA + ")");
    file.writeByte(OBJ_DATA);
if(DEBUG_WR_DATA)System.err.println("wrSglSet: objLen (" + objLen + ")");
    file.writeInt(objLen);
if(DEBUG_WR_DATA)System.err.println("wrSglSet: DATA_SINGLETON_SET (" + DATA_SINGLETON_SET + ")");
    file.writeByte(DATA_SINGLETON_SET);

if(DEBUG_WR_DATA)System.err.println("wrSglSet: FLD_SAMPLE (" + FLD_SAMPLE + ")");
    file.writeByte(FLD_SAMPLE);
    BinaryRealTuple.write(writer, sampleType, sampleReals,
                          sample.getCoordinateSystem(), sample, token);

    if (csIndex >= 0) {
if(DEBUG_WR_DATA)System.err.println("wrSglSet: FLD_INDEX_COORDSYS (" + FLD_INDEX_COORDSYS + ")");
      file.writeByte(FLD_INDEX_COORDSYS);
if(DEBUG_WR_DATA)System.err.println("wrSglSet: coord sys Index (" + csIndex + ")");
      file.writeInt(csIndex);
    }

    if (unitsIndex != null) {
if(DEBUG_WR_DATA)System.err.println("wrSglSet: FLD_INDEX_UNITS (" + FLD_INDEX_UNITS + ")");
      file.writeByte(FLD_INDEX_UNITS);
      BinaryIntegerArray.write(writer, unitsIndex, token);
    }

    if (errorsIndex != null) {
if(DEBUG_WR_DATA)System.err.println("wrSglSet: FLD_INDEX_ERRORS (" + FLD_INDEX_ERRORS + ")");
      file.writeByte(FLD_INDEX_ERRORS);
      BinaryIntegerArray.write(writer, errorsIndex, token);
    }

if(DEBUG_WR_DATA)System.err.println("wrSglSet: FLD_END (" + FLD_END + ")");
    file.writeByte(FLD_END);
  }
}
