
typedef struct {
    int x;
    int h;
} dot_t;
typedef struct {
    int *p;
    int n;
    int max;
} heap_t;
typedef struct {
    int **p;
    int sz;
    int n;
} res_t;
int cmp(void const *a, void const *b) {
    return  ((dot_t *)a)->x < ((dot_t *)b)->x ? -1 :
            ((dot_t *)a)->x > ((dot_t *)b)->x ?  1 :
            ((dot_t *)b)->h < ((dot_t *)a)->h ? -1 : 1;
}
void res_init(res_t *res) {
    res->sz = 100;
    res->n = 0;
    res->p = malloc(res->sz * sizeof(int *));
    //assert(res->p);
}
void add2res(res_t *res, int x, int h) {
    int *p = malloc(2 * sizeof(int));
    //assert(p);
    p[0] = x;
    p[1] = h;
    
    if (res->sz == res->n) {
        res->sz *= 2;
        res->p = realloc(res->p, res->sz * sizeof(int *));
        //assert(res->p);
    }
    res->p[res->n ++] = p;
}
void heap_init(heap_t *heap, int sz) {
    heap->n = 0;
    heap->p = malloc(sz * sizeof(int));
    heap->max = 0;
    //assert(heap->p);
}
int add2heap(heap_t *heap, int k) {
    heap->p[heap->n ++] = k;
    if (k > heap->max) heap->max = k;
    return heap->max;
}
int remove_heap(heap_t *heap, int k) {
    // TODO: better to keep heap data are sorted.
    int i, j, m = 0;
    for (i = 0; i < heap->n; i ++) {
        if (heap->p[i] == k) {
            heap->p[i] = heap->p[-- heap->n];
            if (heap->max == k) {
                for (j = i; j < heap->n; j ++) {
                    if (m < heap->p[j]) m = heap->p[j];
                }
                heap->max = m;
            }
            return heap->max;
        }
        if (m < heap->p[i]) m = heap->p[i];
    }
    return heap->max;
}
int** getSkyline(int** buildings, int buildingsRowSize, int buildingsColSize, int* returnSize) {
    dot_t *dots, *p;
    res_t res;
    heap_t heap;
    int x1, x2, h, prev;
    int n, i;
    
    res_init(&res);
    heap_init(&heap, buildingsRowSize + 1);
    
    n = buildingsRowSize * 2;
    dots = malloc(n * sizeof(dot_t));
    //assert(dots);
    
    // split all buildings to left and right dots
    for (i = 0; i < buildingsRowSize; i ++) {
        x1 = buildings[i][0];
        x2 = buildings[i][1];
        h  = buildings[i][2];
        
        p = &dots[i * 2 + 0];
        p->x = x1;
        p->h = h;

        p = &dots[i * 2 + 1];
        p->x = x2;
        p->h = 0 - h;
    }
    
    qsort(dots, n, sizeof(dot_t), cmp);
    
    prev = add2heap(&heap, 0);
    // for each dot...
    for (i = 0; i < n; i ++) {
        p = &dots[i];
        if (p->h > 0) {
            // add p->h to heap
            h = add2heap(&heap, p->h);
        } else {
            // remove p->h from heap
            h = remove_heap(&heap, 0 - p->h);
        }
        if (prev != h) {
            // there is a change on height
            add2res(&res, p->x, h);
            prev = h;
        }
    }
    
    free(dots);
    free(heap.p);
    
    *returnSize = res.n;
    return res.p;
}

