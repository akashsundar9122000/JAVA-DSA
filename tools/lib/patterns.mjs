// Canonical algorithm-pattern taxonomy and assignment.
//
// The base is NeetCode's pattern list, because that is also how the curated
// playlists and the Blind-75 / NeetCode-150 lists are organized. Extended with
// the patterns this repo genuinely needs and NeetCode has no bucket for
// (Prefix Sum, Sorting, Queue, Recursion, Data Structures), so every file lands
// somewhere honest instead of being swept into "Arrays & Hashing".
//
// NeetCode's "JavaScript" bucket is deliberately dropped: it is a language
// track, not an algorithm pattern, and nothing in a Java repo belongs there.

/**
 * Display order is roughly a learning path — the order these are usually
 * taught and the order the playlists follow — not alphabetical.
 * `blurb` answers "when do I reach for this?", which is the thing worth
 * revising when you have forgotten a pattern entirely.
 */
export const PATTERNS = [
  {
    id: 'arrays-hashing', name: 'Arrays & Hashing',
    blurb: 'Trade memory for time: a hash map turns a repeated "have I seen this?" scan into one pass. The default first idea for counting, de-duplicating and lookup problems.',
  },
  {
    id: 'two-pointers', name: 'Two Pointers',
    blurb: 'Two indices walking a sorted or symmetric structure. Replaces a nested loop with a single pass when moving one end can only help or only hurt.',
  },
  {
    id: 'sliding-window', name: 'Sliding Window',
    blurb: 'A contiguous run whose ends only ever move forward. Use it for "longest/shortest subarray satisfying X" — expand to include, shrink to restore the condition.',
  },
  {
    id: 'prefix-sum', name: 'Prefix Sum',
    blurb: 'Precompute cumulative totals so any range sum is one subtraction. The go-to when queries are many and the array does not change.',
  },
  {
    id: 'stack', name: 'Stack',
    blurb: 'Last-in-first-out, and the monotonic variant. Matching brackets, evaluating expressions, and "next greater element" all reduce to a stack.',
  },
  {
    id: 'queue', name: 'Queue',
    blurb: 'First-in-first-out, plus circular buffers and deques. Underpins BFS and any sliding-window-maximum problem.',
  },
  {
    id: 'binary-search', name: 'Binary Search',
    blurb: 'Halve the search space each step. Works on any monotonic predicate, not just sorted arrays — including binary searching the answer itself.',
  },
  {
    id: 'sorting', name: 'Sorting',
    blurb: 'The comparison sorts and their trade-offs, plus cyclic sort for permutations of 1..n. Often the cheapest way to expose structure a problem depends on.',
  },
  {
    id: 'linked-list', name: 'Linked List',
    blurb: 'Pointer rewiring, fast/slow pointers, and dummy heads. Cycle detection and mid-point finding are the recurring tricks.',
  },
  {
    id: 'trees', name: 'Trees',
    blurb: 'Recursive structure, so recursive solutions. In-order traversal of a BST yields sorted order — the fact most tree problems lean on.',
  },
  {
    id: 'tries', name: 'Tries',
    blurb: 'A tree keyed by character, giving prefix lookup in the length of the word rather than the size of the dictionary.',
  },
  {
    id: 'heap', name: 'Heap / Priority Queue',
    blurb: 'Cheap access to the smallest or largest element while the set keeps changing. The answer to almost every "top k" question.',
  },
  {
    id: 'recursion', name: 'Recursion',
    blurb: 'A base case plus a strictly smaller subproblem. The foundation the whole backtracking and divide-and-conquer family is built on.',
  },
  {
    id: 'backtracking', name: 'Backtracking',
    blurb: 'Try a choice, recurse, then undo it. Generates subsets, permutations and board solutions; forgetting the undo is the classic bug.',
  },
  {
    id: 'graphs', name: 'Graphs',
    blurb: 'DFS and BFS over a grid or an adjacency list. Islands, flood fill, connected components and shortest hops on an unweighted graph.',
  },
  {
    id: 'advanced-graphs', name: 'Advanced Graphs',
    blurb: 'Weighted and ordered graphs: Dijkstra, topological sort, union-find, minimum spanning trees.',
  },
  {
    id: 'dp-1d', name: '1-D Dynamic Programming',
    blurb: 'Overlapping subproblems along one axis. Recognise it when naive recursion recomputes the same state — then memoise or build a table.',
  },
  {
    id: 'dp-2d', name: '2-D Dynamic Programming',
    blurb: 'Two changing dimensions, usually two sequences or a grid. Edit distance, knapsack and longest common subsequence.',
  },
  {
    id: 'greedy', name: 'Greedy',
    blurb: 'Take the locally best option and never reconsider. Fast when it works, wrong when it does not — the real skill is proving it applies.',
  },
  {
    id: 'intervals', name: 'Intervals',
    blurb: 'Sort by start or end, then sweep. Merging, inserting and detecting overlap.',
  },
  {
    id: 'math-geometry', name: 'Math & Geometry',
    blurb: 'Digit manipulation, matrix rotation, and number theory. Usually a closed-form insight rather than a search.',
  },
  {
    id: 'bit-manipulation', name: 'Bit Manipulation',
    blurb: 'XOR cancellation, masks and shifts. Turns some counting and pairing problems into constant space.',
  },
  {
    id: 'data-structures', name: 'Data Structures',
    blurb: 'Building the structures themselves — stacks, queues, heaps, balanced trees — rather than applying them. Design questions test exactly this.',
  },
];

export const PATTERN_BY_NAME = new Map(PATTERNS.map((p) => [p.name, p]));
export const PATTERN_BY_ID = new Map(PATTERNS.map((p) => [p.id, p]));

/** NeetCode pattern name -> our id. Names match except where we split. */
const FROM_NEETCODE = {
  'Arrays & Hashing': 'arrays-hashing',
  'Two Pointers': 'two-pointers',
  'Sliding Window': 'sliding-window',
  'Stack': 'stack',
  'Binary Search': 'binary-search',
  'Linked List': 'linked-list',
  'Trees': 'trees',
  'Tries': 'tries',
  'Heap / Priority Queue': 'heap',
  'Backtracking': 'backtracking',
  'Graphs': 'graphs',
  'Advanced Graphs': 'advanced-graphs',
  '1-D Dynamic Programming': 'dp-1d',
  '2-D Dynamic Programming': 'dp-2d',
  'Greedy': 'greedy',
  'Intervals': 'intervals',
  'Math & Geometry': 'math-geometry',
  'Bit Manipulation': 'bit-manipulation',
  // 'JavaScript' intentionally absent — a language track, not a pattern.
};

/**
 * LeetCode topic tags -> our id, MOST SPECIFIC FIRST.
 *
 * Order is the whole point. LeetCode tags a sliding-window problem
 * ['Array', 'Sliding Window']; scanning in this order resolves it to Sliding
 * Window rather than to the near-meaningless 'Array'.
 */
const FROM_TAGS = [
  ['Sliding Window', 'sliding-window'],
  ['Trie', 'tries'],
  ['Union Find', 'advanced-graphs'],
  ['Topological Sort', 'advanced-graphs'],
  ['Shortest Path', 'advanced-graphs'],
  ['Minimum Spanning Tree', 'advanced-graphs'],
  ['Strongly Connected Component', 'advanced-graphs'],
  ['Eulerian Circuit', 'advanced-graphs'],
  ['Backtracking', 'backtracking'],
  ['Monotonic Stack', 'stack'],
  ['Monotonic Queue', 'queue'],
  ['Binary Search', 'binary-search'],
  ['Binary Search Tree', 'trees'],
  ['Binary Indexed Tree', 'advanced-graphs'],
  ['Segment Tree', 'data-structures'],
  ['Binary Tree', 'trees'],
  ['Tree', 'trees'],
  ['Heap (Priority Queue)', 'heap'],
  ['Linked List', 'linked-list'],
  ['Graph', 'graphs'],
  ['Depth-First Search', 'graphs'],
  ['Breadth-First Search', 'graphs'],
  ['Matrix', 'graphs'],
  ['Prefix Sum', 'prefix-sum'],
  ['Two Pointers', 'two-pointers'],
  ['Stack', 'stack'],
  ['Queue', 'queue'],
  ['Design', 'data-structures'],
  ['Bit Manipulation', 'bit-manipulation'],
  ['Bitmask', 'bit-manipulation'],
  ['Dynamic Programming', 'dp-1d'],
  ['Greedy', 'greedy'],
  ['Divide and Conquer', 'recursion'],
  ['Recursion', 'recursion'],
  ['Memoization', 'dp-1d'],
  ['Sorting', 'sorting'],
  ['Merge Sort', 'sorting'],
  ['Bucket Sort', 'sorting'],
  ['Counting Sort', 'sorting'],
  ['Radix Sort', 'sorting'],
  ['Quickselect', 'sorting'],
  ['Geometry', 'math-geometry'],
  ['Math', 'math-geometry'],
  ['Number Theory', 'math-geometry'],
  ['Combinatorics', 'math-geometry'],
  ['Hash Table', 'arrays-hashing'],
  ['Counting', 'arrays-hashing'],
  ['String', 'arrays-hashing'],
  ['Array', 'arrays-hashing'],
];

/**
 * Folder -> our id. Every file lives in a folder, so this is the backstop that
 * makes the cascade total. `className` is consulted where one folder holds two
 * patterns: StackAndQueue is genuinely both.
 */
function fromFolder(categoryPath, className = '') {
  const direct = {
    'BinarySearch': 'binary-search',
    'BitManipulation': 'bit-manipulation',
    'Graph': 'graphs',
    'Heap': 'heap',
    'LinkedList': 'linked-list',
    'LinkedList/Basic': 'linked-list',
    'PrefixSum': 'prefix-sum',
    'Recursion': 'recursion',
    'Recursion/BackTracking': 'backtracking',
    'SlidingWindow': 'sliding-window',
    'Sorting': 'sorting',
    'Trees': 'trees',
    'TwoPointer': 'two-pointers',
  };
  if (direct[categoryPath]) return direct[categoryPath];

  if (categoryPath === 'StackAndQueue') {
    return /queue/i.test(className) ? 'queue' : 'stack';
  }
  // Mixed folders (Leetcode, LeetcodeContest, GFG, CompanyQuestions/*) have no
  // inherent pattern; their problems are resolved by tags above, and anything
  // still unresolved is an array/string problem in practice.
  return 'arrays-hashing';
}

/**
 * Resolve one problem's pattern.
 *
 * Cascade, first hit wins, and `patternSource` records which rung fired so the
 * result stays auditable the same way titleSource is.
 *
 * @returns {{pattern: string, patternSource: string}}
 */
export function resolvePattern({ override, neetcodePattern, topicTags, categoryPath, className }) {
  if (override && PATTERN_BY_ID.has(override)) {
    return { pattern: override, patternSource: 'override' };
  }

  if (neetcodePattern && FROM_NEETCODE[neetcodePattern]) {
    return { pattern: FROM_NEETCODE[neetcodePattern], patternSource: 'neetcode' };
  }

  if (topicTags?.length) {
    const names = new Set(topicTags);
    for (const [tag, id] of FROM_TAGS) {
      if (names.has(tag)) return { pattern: id, patternSource: 'leetcode-tags' };
    }
  }

  return { pattern: fromFolder(categoryPath, className), patternSource: 'folder' };
}
