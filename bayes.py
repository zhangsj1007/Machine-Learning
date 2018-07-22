import numpy;
import  re;


def loadDataSet():
    postingList=[['my', 'dog', 'has', 'flea', \
                'problems', 'help', 'please'],
                ['maybe', 'not', 'take', 'him', \
                'to', 'dog', 'park', 'stupid'],
                ['my', 'dalmation', 'is', 'so', 'cute', \
                'I', 'love', 'him'],
                ['stop', 'posting', 'stupid', 'worthless', 'garbage'],
                ['mr', 'licks', 'ate', 'my', 'steak', 'how',\
                'to', 'stop', 'him'],
                ['quit', 'buying', 'worthless', 'dog', 'food', 'stupid']]
    classVec = [0,1,0,1,0,1]
    return postingList,classVec

def createVocabList(dataset) :
    vocabSet = set([])
    for doc in dataset :
        vocabSet = vocabSet | set(doc)
    return list(vocabSet)

def bagOfWords2Vec(doc, vocabList) :
    retVec = [0] * len(vocabList)
    for word in doc :
        if word in vocabList :
            retVec[vocabList.index(word)] += 1
        else :
            print "One word %s is not in my vocabulary" % word
    return retVec

def trainNB(trainMatrix, trainCategory) :
    numWords = len(trainMatrix[0])
    numVecotors = len(trainCategory)

    pAbuse = sum(trainCategory) / float(numVecotors)

    vec0Cnt = numpy.ones(numWords)
    vec1Cnt = numpy.ones(numWords)

    sum0Cnt = 2
    sum1Cnt = 2

    for i in range(numVecotors) :
        if trainCategory[i] == 0 :
            vec0Cnt += trainMatrix[i]
            sum0Cnt += sum(trainMatrix[i])
        else :
            vec1Cnt += trainMatrix[i]
            sum1Cnt += sum(trainMatrix[i])

    p0Vec = numpy.log(vec0Cnt / float(sum0Cnt))
    p1Vec = numpy.log(vec1Cnt / float(sum1Cnt))

    return p0Vec, p1Vec, pAbuse

def classifyNB(docVec, p0Vec, p1Vec, pAbuse) :
    p0 = sum(docVec * p0Vec) + numpy.log(1.0 - pAbuse)
    p1 = sum(docVec * p1Vec) + numpy.log(pAbuse)

    if p0 > p1 :
        return 0
    else:
        return 1

def textParse(bigString) :
    regEx = re.compile(r'\W*')
    listOftokens = regEx.split(bigString)
    return [tok.lower() for tok in listOftokens if len(tok) > 2]

def spamTest() :
    docList = []
    classList = []
    for i in range(1, 26) :
        wordsList = textParse(open('/Users/songjianzhang/Code/machinelearninginaction/Ch04/email/spam/%d.txt' % i).read())
        docList.append(wordsList)
        classList.append(1)
        wordsList = textParse(open('/Users/songjianzhang/Code/machinelearninginaction/Ch04/email/ham/%d.txt' % i).read())
        docList.append(wordsList)
        classList.append(0)

    vocabList = createVocabList(docList)

    #range(50),return list
    trainingSet = range(50)
    testSet = []

    for i in range(10) :
        import random;
        #from list shift one randomly
        randIdx = int(random.uniform(0, len(trainingSet)))
        del(trainingSet[randIdx])
        testSet.append(randIdx)

    trainMat = []
    trainClass = []
    for i in trainingSet :
        trainMat.append(bagOfWords2Vec(docList[i], vocabList))
        trainClass.append(classList[i])

    p0Vec, p1Vec, pAbuse = trainNB(trainMat, trainClass)

    errorCnt = 0

    for i in testSet :
        doc = docList[i]
        vec = bagOfWords2Vec(doc, vocabList)
        c = classifyNB(vec, p0Vec, p1Vec, pAbuse)
        if c != classList[i] :
            errorCnt += 1

    print('Spam email detector error rate is %f' % (errorCnt / float(10)))




if  __name__ == "__main__" :
    spamTest()