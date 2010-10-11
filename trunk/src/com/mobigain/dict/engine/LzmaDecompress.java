package com.mobigain.dict.engine;

public class LzmaDecompress 
{
	final static int  INDEX_PROPERTIES		= 0;
	final static int INDEX_OUT_SIZE			= 5;
	final static int INDEX_ZERO				= 9;
	final static int LZMA_HEADER_SIZE		= 13;

	final static int LZMA_BASE_SIZE			= 1846;
	final static int LZMA_LIT_SIZE			= 768;
	final static int SIZE_OF_SHORT			= 2;

	final static int kNumTopBits			= 24;
	final static int kTopValue				= ( 1 << kNumTopBits);

	final static int kNumBitModelTotalBits	= 11;
	final static int kBitModelTotal			= (1 << kNumBitModelTotalBits);
	final static int kNumMoveBits			= 5;

	final static int kNumPosBitsMax			= 4;
	final static int kNumPosStatesMax		= (1 << kNumPosBitsMax);

	final static int kLenNumLowBits			= 3;
	final static int kLenNumLowSymbols		= (1 << kLenNumLowBits);
	final static int kLenNumMidBits			= 3;
	final static int kLenNumMidSymbols		= (1 << kLenNumMidBits);
	final static int kLenNumHighBits		= 8;
	final static int kLenNumHighSymbols		= (1 << kLenNumHighBits);

	final static int LenChoice				= 0;
	final static int LenChoice2				= (LenChoice + 1);
	final static int LenLow					= (LenChoice2 + 1);
	final static int LenMid					= (LenLow + (kNumPosStatesMax << kLenNumLowBits));
	final static int LenHigh				= (LenMid + (kNumPosStatesMax << kLenNumMidBits));
	final static int kNumLenProbs			= (LenHigh + kLenNumHighSymbols);

	final static int kNumStates				= 12;

	final static int kStartPosModelIndex	= 4;
	final static int kEndPosModelIndex		= 14;
	final static int kNumFullDistances		=  (1 << (kEndPosModelIndex >> 1));

	final static int kNumPosSlotBits		= 6;
	final static int kNumLenToPosStates		= 4;

	final static int kNumAlignBits			= 4;
	final static int kAlignTableSize		= (1 << kNumAlignBits);

	final static int kMatchMinLen			= 2;

	final static int IsMatch				= 0;
	final static int IsRep					= (IsMatch + (kNumStates << kNumPosBitsMax));
	final static int IsRepG0				= (IsRep + kNumStates);
	final static int IsRepG1				= (IsRepG0 + kNumStates);
	final static int IsRepG2				= (IsRepG1 + kNumStates);
	final static int IsRep0Long				= (IsRepG2 + kNumStates);
	final static int PosSlot				= (IsRep0Long + (kNumStates << kNumPosBitsMax));
	final static int SpecPos				= (PosSlot + (kNumLenToPosStates << kNumPosSlotBits));
	final static int Align					= (SpecPos + kNumFullDistances - kEndPosModelIndex);
	final static int LenCoder				= (Align + kAlignTableSize);
	final static int RepLenCoder			= (LenCoder + kNumLenProbs);
	final static int Literal				= (RepLenCoder + kNumLenProbs);

	final static int LZMAInternalData_MAX_SIZE		= (10 * 1024);
	/////////////////////////////////////////////////////////////////////////////////////////////////////
	static short[] _lzmaInternalBuffer; 

	public static byte[] _inBuffer;
	public static byte[] _outBuffer; 
	static int _inBufferIndex;

	static long _lzmaCode;
	static long _lzmaRange;

	// //////////////////////////////////////////////////////////////////////////////////////////////////

	static long LZMA_RangeDecoderDecodeDirectBits(int numTotalBits) {
		long result = 0;

		for (int i = numTotalBits; i > 0; i--) {
			_lzmaRange >>= 1;
			result <<= 1;

			if (_lzmaCode >= _lzmaRange) {
				_lzmaCode -= _lzmaRange;
				result |= 1;
			}

			if (_lzmaRange < kTopValue) {
				_lzmaRange <<= 8;
				_lzmaCode = (_lzmaCode << 8)
						| (_inBuffer[_inBufferIndex++] & 0xFF);
			}
		}

		return result & (long) 0xFFFFFFFF;
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////

	static int LZMA_RangeDecoderBitDecode(int index) {
		int prob = _lzmaInternalBuffer[index] & 0xFFFF;

		long bound = (_lzmaRange >> kNumBitModelTotalBits) * prob;
		int rez = 0;

		if (_lzmaCode < bound) {
			_lzmaRange = bound;
			prob += (kBitModelTotal - prob) >> kNumMoveBits;
		} else {
			_lzmaRange -= bound;
			_lzmaCode -= bound;

			prob -= prob >> kNumMoveBits;

			rez = 1;
		}

		if (_lzmaRange < kTopValue) {
			_lzmaCode = (_lzmaCode << 8) | (_inBuffer[_inBufferIndex++] & 0xFF);
			_lzmaRange <<= 8;
		}

		_lzmaInternalBuffer[index] = (short) prob;
		return rez;
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////

	static int LZMA_RangeDecoderBitTreeDecode(int index, int numLevels) {
		int mi = 1;
		int i;

		for (i = numLevels; i > 0; i--) {
			mi = (mi + mi) + LZMA_RangeDecoderBitDecode(index + mi);
		}

		return mi - (1 << numLevels);
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////

	static int LZMA_RangeDecoderReverseBitTreeDecode(int index, int numLevels) {
		int mi = 1;
		int i;
		int symbol = 0;

		for (i = 0; i < numLevels; i++) {
			int bit = LZMA_RangeDecoderBitDecode(index + mi);
			mi = mi + mi + bit;
			symbol |= (bit << i);
		}

		return symbol;
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////

	static int LZMA_LenDecode(int index, int posState) {
		if (LZMA_RangeDecoderBitDecode(index + LenChoice) == 0) {
			return LZMA_RangeDecoderBitTreeDecode(index + LenLow
					+ (posState << kLenNumLowBits), kLenNumLowBits);
		}

		if (LZMA_RangeDecoderBitDecode(index + LenChoice2) == 0) {
			return kLenNumLowSymbols
					+ LZMA_RangeDecoderBitTreeDecode(index + LenMid
							+ (posState << kLenNumMidBits), kLenNumMidBits);
		}

		return kLenNumLowSymbols
				+ kLenNumMidSymbols
				+ LZMA_RangeDecoderBitTreeDecode(index + LenHigh,
						kLenNumHighBits);
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////
	// 0..4 - properties
	// 5..8 - out size
	// 9..12 - zero

	static byte[] LZMA_Decompress(byte[] inStream) {
		int outSize = 0;
		int i;
		int prop0;
		int lc, lp, pb;

		byte[] outStream = null;

		if (inStream == null)
			return null;

		_inBuffer = inStream;

		for (i = 0; i < 4; i++) {
			outSize += (int) (_inBuffer[i + INDEX_OUT_SIZE] & 0xFF) << (i * 8);
		}

		prop0 = _inBuffer[INDEX_PROPERTIES] & 0xFF;

		for (pb = 0; prop0 >= (9 * 5); pb++, prop0 -= (9 * 5))
			;
		for (lp = 0; prop0 >= 9; lp++, prop0 -= 9)
			;
		lc = prop0;

		if (_lzmaInternalBuffer == null) {
			_lzmaInternalBuffer = new short[LZMAInternalData_MAX_SIZE];
		}
		if (outStream == null) {
			outStream = new byte[outSize];
		}
		// outStream = _outBuffer;

		int numProbs = Literal + ((int) LZMA_LIT_SIZE << (lc + lp));

		int state = 0;
		int previousIsMatch = 0;
		byte previousByte = 0;
		int rep0 = 1, rep1 = 1, rep2 = 1, rep3 = 1;
		int nowPos = 0;
		int posStateMask = (1 << pb) - 1;
		int literalPosMask = (1 << lp) - 1;
		int len = 0;

		// _lzmaInternalBuffer = new short[LZMAInternalData_MAX_SIZE];

		for (i = 0; i < numProbs; i++) {
			_lzmaInternalBuffer[i] = (short) (kBitModelTotal >> 1);
		}

		_inBufferIndex = LZMA_HEADER_SIZE;

		_lzmaCode = 0;
		_lzmaRange = 0x0FFFFFFFFL;

		for (i = 0; i < 5; i++) {
			_lzmaCode = (_lzmaCode << 8) | (_inBuffer[_inBufferIndex++] & 0xFF);
		}

		while (nowPos < outSize) {
			int posState = (int) (nowPos & posStateMask);

			if (LZMA_RangeDecoderBitDecode(IsMatch + (state << kNumPosBitsMax)
					+ posState) == 0) {
				int index = Literal
						+ (LZMA_LIT_SIZE * ((((nowPos) & literalPosMask) << lc) + ((previousByte & 0xFF) >> (8 - lc))));

				if (state < 4) {
					state = 0;
				} else if (state < 10) {
					state -= 3;
				} else {
					state -= 6;
				}

				if (previousIsMatch != 0) {
					i = 1;
					previousIsMatch = outStream[nowPos - rep0] & 0xFF;

					do {
						int bit;
						int matchBit = (previousIsMatch >> 7) & 1;
						previousIsMatch <<= 1;

						bit = LZMA_RangeDecoderBitDecode(index
								+ ((1 + matchBit) << 8) + i);
						i = (i << 1) | bit;

						if (matchBit != bit) {
							while (i < 0x100) {
								i = (i + i)
										| LZMA_RangeDecoderBitDecode(index + i);
							}
							break;
						}
					} while (i < 0x100);

					previousByte = (byte) i;

					previousIsMatch = 0;
				} else {
					// previousByte = LzmaLiteralDecode(index);
					i = 1;

					do {
						i = (i + i) | LZMA_RangeDecoderBitDecode(index + i);
					} while (i < 0x100);

					previousByte = (byte) i;
				}

				outStream[nowPos++] = previousByte;
			} else {
				previousIsMatch = 1;
				if (LZMA_RangeDecoderBitDecode(IsRep + state) == 1) {
					if (LZMA_RangeDecoderBitDecode(IsRepG0 + state) == 0) {
						if (LZMA_RangeDecoderBitDecode(IsRep0Long
								+ (state << kNumPosBitsMax) + posState) == 0) {
							state = state < 7 ? 9 : 11;

							previousByte = outStream[nowPos - rep0];

							outStream[nowPos++] = previousByte;
							continue;
						}
					} else {
						int distance;
						if (LZMA_RangeDecoderBitDecode(IsRepG1 + state) == 0) {
							distance = rep1;
						} else {
							if (LZMA_RangeDecoderBitDecode(IsRepG2 + state) == 0) {
								distance = rep2;
							} else {
								distance = rep3;
								rep3 = rep2;
							}
							rep2 = rep1;
						}
						rep1 = rep0;
						rep0 = distance;
					}
					len = LZMA_LenDecode(RepLenCoder, posState);
					state = state < 7 ? 8 : 11;
				} else {
					int posSlot;
					rep3 = rep2;
					rep2 = rep1;
					rep1 = rep0;
					state = state < 7 ? 7 : 10;

					len = LZMA_LenDecode(LenCoder, posState);
					posSlot = LZMA_RangeDecoderBitTreeDecode(
							PosSlot
									+ ((len < kNumLenToPosStates ? len
											: kNumLenToPosStates - 1) << kNumPosSlotBits),
							kNumPosSlotBits);

					if (posSlot >= 4) {
						int numDirectBits = ((posSlot >> 1) - 1);
						rep0 = ((2 | ((int) posSlot & 1)) << numDirectBits);
						if (posSlot < kEndPosModelIndex) {
							rep0 += LZMA_RangeDecoderReverseBitTreeDecode(
									SpecPos + rep0 - posSlot - 1, numDirectBits);
						} else {
							rep0 += LZMA_RangeDecoderDecodeDirectBits(numDirectBits - 4) << kNumAlignBits;
							rep0 += LZMA_RangeDecoderReverseBitTreeDecode(
									Align, kNumAlignBits);
						}
					} else {
						rep0 = posSlot;
					}
					rep0++;
				}
				if (rep0 == 0) {
					// it's for stream version
					len = -1;
					break;
				}

				len += 2;
				do {
					previousByte = outStream[nowPos - rep0];

					outStream[nowPos++] = previousByte;
					len--;
				} while (len > 0 && nowPos < outSize);
			}
		}
		// _lzmaInternalBuffer = null;
		// _inBuffer = null;
		// System.gc();

		return outStream;
	}

}
