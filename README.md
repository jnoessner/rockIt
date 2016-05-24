# RockIt: A query engine for Markov logic

RockIt answers maximum a-posteriori (MAP) queries (also called MPE queries), marginal queries (also called probability queries), and learns the weights of Markov logic networks. Markov logic is a combination of Markov networks and first-order logic. RockIt is currently the fastest MAP query engine for Markov logic networks.

**RockIt is also available as a [web service](http://executor.informatik.uni-mannheim.de/systems/rockit/). All you have to do is upload your MLN files.**

### Maximum A-Posteriori (MAP) Inference

RockIt compiles MAP queries to integer linear programs. During this translation we apply the meta algorithms cutting plane 
inference (CPI) and cutting plane aggregation (CPA). In CPI we only add the constraints which are violated and solve several 
smaller ILPs until no new violated constraints are found. This usually leads to much smaller ILPs and, thus, faster run times. 
In CPA we leverage symmetries in the Markov logic network by aggregating ground clauses. This reduces the required number of 
constraints and variables in the ILP translation and makes symmetries more explicit to state-of-the-art ILP solvers. 
Internally, RockIt utilizes in-memory databases from MySQL and the integer linear solver Gurobi 
(a free academic license is available).

The theoretical foundations of the MAP inference engine and some experimental results are presented in the paper 
> [RockIt: Exploiting Parallelism and Symmetry for MAP Inference in Statistical Relational Models that appeared in the proceedings of AAAI 2013](https://www.aaai.org/ocs/index.php/WS/AAAIW13/paper/download/7049/6639).

### Marginal Inference

We implemented a fast Gibbs sampler which starts with the MAP state as consistent world. In each iteration we take one literal l. 
Then, we swap l and check if any hard constraint, any cardinality constraint, or any existential constraint is violated. 
If not, we swap l with a certain probability which depends on the input weights. Efficient data structures and intelligent 
storing of the results makes our Gibbs sampling very fast and memory efficient. For details, we refer to our documentation.

In the near future, we will extend the Gibbs sampler to leverage symmetries as described in Symmetry-Aware Marginal Density Estimation.

### Documentation

We compiled a documentation including simple installation instructions.

RockIt's syntax is almost identical to that of existing Markov logic systems such as Alchemy and Tuffy. The main differences are:

1.  A predicate definition preceded by * is considered to have the closed world assumption; i.e., all ground atoms of this predicate not listed in the evidence are false. (Example: *friends(Human,Human))
2.  In RockIt, we do not need to define query predicates explicitly, because every predicate not preceded by * is a query predicate.
3.  Variable names can also start with a capital letter. Constants must be enclosed with double quotation marks (Example: "Constant").
4.  In RockIt, it is not possible to use implications (=>) and conjunctions (^). The user has to transform her formula to CNF (http://en.wikipedia.org/wiki/Conjunctive_normal_form) such that they only contain disjunctions (v).
5.  The syntax of existential formulas differs from the syntax of Tuffy and Alchemy. Please refer to our documentation for details.

### Using RockIt

If you want to use rockIt you have several possibilities:

1. Access RockIt via our web interface. Since you merely have to upload your MLN files, this is probably the best way to get started and to test how well RockIt can process your MLNs.
2. Install the binaries on your machine
3. There are also installations instructions available
4. Alternatively, you can download the source code. The main class is located in com.googlecode.rockit.app.Main.

### Plans for future additions

1.  Complete the existing documentation.
2.  Further optimizations in speed.

### Support

We offer support. You get support by either writing a mail to jan.noessner@gmail.com or by posting issues in the Issue Tracker. 
The second possibility is preferred. 
Support does also include help with defining models or enhancement wishes for rockIt. 
So if you have any problems or questions concerning rockIt just ask!

### Feedback

If you use Rockit for your project and it has been helpful (or not) we would be happy to hear about it. If you use RockIt in your research, please [cite us](http://dblp.uni-trier.de/rec/bibtex/conf/aaai/NoessnerNS13)! We would also be happy if you could send us a copy of any published work that uses rockIt.
