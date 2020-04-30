package com.jstarcraft.nlp.lucene.mmseg;

import java.io.IOException;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chenlb.mmseg4j.MMSeg;
import com.chenlb.mmseg4j.Seg;
import com.chenlb.mmseg4j.Word;

/**
 * mmseg4j分词器
 * 
 * @author Birdy
 *
 */
public final class MmsegTokenizer extends Tokenizer {

    private static final Logger LOGGER = LoggerFactory.getLogger(MmsegTokenizer.class);

    /** 词元 **/
    private final CharTermAttribute termAttribute = addAttribute(CharTermAttribute.class);
    /** 位移 **/
    private final OffsetAttribute offsetAttribute = addAttribute(OffsetAttribute.class);
    /** 距离 **/
    private final PositionIncrementAttribute positionAttribute = addAttribute(PositionIncrementAttribute.class);
    /** 词性 **/
    private final TypeAttribute typeAttribute = addAttribute(TypeAttribute.class);

    private ThreadLocal<MMSeg> mmSeg;

    public MmsegTokenizer(Seg seg) {
        super();
        mmSeg = new ThreadLocal<>();
        mmSeg.set(new MMSeg(input, seg));
    }

    /*
     * //lucene 2.9 以下 public Token next(Token reusableToken) throws IOException { Token token = null; Word word = mmSeg.next(); if(word != null) { //lucene 2.3 reusableToken.clear(); reusableToken.setTermBuffer(word.getSen(), word.getWordOffset(), word.getLength()); reusableToken.setStartOffset(word.getStartOffset()); reusableToken.setEndOffset(word.getEndOffset()); reusableToken.setType(word.getType()); token = reusableToken; //lucene 2.4 //token = reusableToken.reinit(word.getSen(),
     * word.getWordOffset(), word.getLength(), word.getStartOffset(), word.getEndOffset(), word.getType()); } return token; }
     */

    // lucene 2.9/3.0
    @Override
    public boolean incrementToken() throws IOException {
        clearAttributes();
        Word word = mmSeg.get().next();
        if (word != null) {
            // lucene 3.0
            // termAtt.setTermBuffer(word.getSen(), word.getWordOffset(), word.getLength());
            // lucene 3.1
            termAttribute.copyBuffer(word.getSen(), word.getWordOffset(), word.getLength());
            offsetAttribute.setOffset(word.getStartOffset(), word.getEndOffset());
            typeAttribute.setType(word.getType());
            return true;
        } else {
            end();
            return false;
        }
    }

    @Override
    public void reset() throws IOException {
        super.reset();
        // lucene 4.0
        // org.apache.lucene.analysis.Tokenizer.setReader(Reader)
        // setReader 自动被调用, input 自动被设置。
        mmSeg.get().reset(input);
    }

}
