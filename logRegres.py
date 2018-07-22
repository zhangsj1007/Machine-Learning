import numpy;
import matplotlib.pyplot as plt;
import random;

def loadDataset() :
    dataMat = []
    labelMat = []
    fr = open('/Users/songjianzhang/Code/machinelearninginaction/Ch05/testSet.txt')
    for line in fr.readlines() :
        lineArr = line.strip().split()
        #this is very convenient in python, in C++ or Java, no operations like this
        dataMat.append([1.0, float(lineArr[0]), float(lineArr[1])])
        labelMat.append(int(lineArr[2]))
    return dataMat, labelMat

def sigmoid(inX) :
    return 1.0 / (1 + numpy.exp(-inX))

def gradAscent(dataMatIn, classLabels) :
    dataMatrix = numpy.mat(dataMatIn)
    classMatrix = numpy.mat(classLabels).transpose()
    m, n = numpy.shape(dataMatrix)
    alpha = 0.001
    maxCycles = 500
    weights = numpy.ones((n, 1))
    for k in range(maxCycles) :
        h = sigmoid(dataMatrix * weights)
        error = (classMatrix - h)
        weights = weights + alpha * dataMatrix.transpose() * error

    return weights

def plotBestFit(wei) :
    weights = wei.getA()
    dataMat, labelMat = loadDataset()
    dataArr = numpy.array(dataMat)
    n = numpy.shape(dataArr)[0]
    xcord1 = [] ; ycord1 = []
    xcord2 = [] ; ycord2 = []
    for i in range(n) :
        if int(labelMat[i]) == 1 :
            xcord1.append(dataArr[i, 1]) ; ycord1.append(dataArr[i, 2])
        else :
            xcord2.append(dataArr[i, 1]) ; ycord2.append(dataArr[i, 2])

    fig = plt.figure()
    ax = fig.add_subplot(111)
    ax.scatter(xcord1, ycord1, s = 30, c = 'red', marker = 's')
    ax.scatter(xcord2, ycord2, s = 30, c = 'green')
    x = numpy.arange(-3.0, 3.0, 0.1)
    y = (-weights[0] - weights[1] * x) / weights[2]
    ax.plot(x, y)
    plt.xlabel('X1');
    plt.ylabel('X2');
    plt.show()

def stocGradAscent(dataMatrix, classLabels):
    m, n = numpy.shape(dataMatrix)
    alpha = 0.01
    weights = numpy.ones((n, 1))
    for i in range(m) :
        h = sigmoid(sum(dataMatrix[i] * weights))
        error = classLabels[i] - h
        weights = weights + numpy.mat(alpha * error * dataMatrix[i]).transpose()
    return weights

def stocGradAscent1(dataMatrix, classLabels, maxCycles = 150):
    m, n = numpy.shape(dataMatrix)
    alpha = 0.01
    weights = numpy.ones((n, 1))
    for i in range(maxCycles) :
        indexes = range(m)
        for j in range(m) :
            alpha = 4 / (i + j + 1.0) + 0.001
            randIndex = int(random.uniform(0, len(indexes)))
            h = sigmoid(sum(dataMatrix[randIndex] * weights))
            error = classLabels[randIndex] - h
            weights = weights + numpy.mat(alpha * error * dataMatrix[randIndex]).transpose()
            del(indexes[randIndex])
    return weights

def classifyVector(inX, weights) :
    prob = sigmoid(sum(inX * weights))
    if prob > 0.5 :
        return 1.0
    else :
        return 0.0

def colicTest() :
    frTrain = open('/Users/songjianzhang/Code/machinelearninginaction/Ch05/horseColicTraining.txt')
    frTest = open('/Users/songjianzhang/Code/machinelearninginaction/Ch05/horseColicTest.txt')
    trainingSet = []
    trainingLebels = []
    for line in frTrain.readlines() :
        lineStr = line.strip().split('\t')
        features = []
        for i in range(21) :
            features.append(float(lineStr[i]))
        trainingSet.append(features)
        trainingLebels.append(float(lineStr[21]))

    weights = stocGradAscent1(numpy.mat(trainingSet), trainingLebels, 500)

    testSet = []
    testLabels = []
    for line in frTest.readlines() :
        lineStr = line.strip().split('\t')
        features = []
        for i in range(21) :
            features.append(float(lineStr[i]))
        testSet.append(features)
        testLabels.append(int(lineStr[21]))

    errorCnt = 0
    sumCnt = len(testLabels)
    for i in range(sumCnt) :
        testClass = classifyVector(numpy.mat(testSet[i]), weights)
        if int(testClass) != int(testLabels[i]) :
            errorCnt += 1

    print ('Colic test, the correct rate = %f ' % (errorCnt / float(sumCnt)))


if __name__ == '__main__' :
    #dataMatIn, labelMatIn = loadDataset()
    #weights = stocGradAscent1(numpy.mat(dataMatIn), labelMatIn)
    #print(weights)
    #plotBestFit(weights)
    colicTest()